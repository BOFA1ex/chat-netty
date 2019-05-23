package com.bofa.client.handler;

import com.bofa.attribute.NoticeStatus;
import com.bofa.attribute.UserStatus;
import com.bofa.client.service.UserFriendSv;
import com.bofa.client.service.UserNoticeSv;
import com.bofa.client.util.H2TaskManager;
import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.entity.UserFriend;
import com.bofa.entity.UserNotice;
import com.bofa.protocol.request.FriendACallBackRequestPacket;
import com.bofa.protocol.response.FriendACallBackResponsePacket;
import com.bofa.util.PrintUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.handler
 * @date 2019/4/28
 */
@ChannelHandler.Sharable
public class FriendACallBackRequestHandler extends SimpleChannelInboundHandler<FriendACallBackRequestPacket> {

    @Autowired
    private UserNoticeSv userNoticeSv;

    @Autowired
    private UserFriendSv userFriendSv;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FriendACallBackRequestPacket requestPacket) throws Exception {
        UserNotice approvalNotice = requestPacket.getUserNotice();

        FriendACallBackResponsePacket responsePacket = handleApprovalCallBack(approvalNotice);
        /**
         * save notice which status is read
         */
        H2TaskManager.execute("save notice", () -> {
            approvalNotice.setNoticestatus(NoticeStatus.READ.status);
            userNoticeSv.save(approvalNotice);
        });
        ctx.channel().writeAndFlush(responsePacket);
    }

    /**
     * online handle directly
     * offline client get notices and need handle it.
     *
     * @param approvalNotice
     * @return
     */
    public FriendACallBackResponsePacket handleApprovalCallBack(UserNotice approvalNotice) {
        String approvalContent = approvalNotice.getNoticecontent();
        String fromUserName = approvalNotice.getUsername();
        approvalNotice.setNoticestatus(NoticeStatus.READ.status);
        PrintStreamDelegate.delegate(() -> {
            PrintUtil.println(fromUserName, "发来好友请求: " + approvalContent);
            PrintUtil.print("接受/拒绝 [y/n] ");
        });
        /**
         * 接受:
         * 1.update h2-database userFriends and session
         * 2.save userFriend in h2-database
         * 3.call back server update approval status
         * 拒绝:
         * 1.call back server update approval status and fail reason
         *
         * h2 save read-notice
         */
        FriendACallBackResponsePacket responsePacket = new FriendACallBackResponsePacket();
        responsePacket.setUserNotice(approvalNotice);
        UserFriend userFriend = mapper(approvalNotice);
        String s = PrintStreamDelegate.nextLine();
        switch (s) {
            case "y":
                responsePacket.setSuccess(true);
                Optional.ofNullable(SessionUtil.getSession(userFriend.getUserId()))
                        .ifPresent(session -> session.getFriends().add(userFriend));
                H2TaskManager.execute("agree to save userFriend", () -> {
                    userFriendSv.save(userFriend);
                });
                break;
            case "n":
                PrintStreamDelegate.delegate(() -> PrintUtil.print("输入拒绝理由: "));
                String rejectReason = PrintStreamDelegate.nextLine();
                responsePacket.setFailReason(rejectReason);
                responsePacket.setSuccess(false);
                break;
            default:
                PrintStreamDelegate.delegate(() -> PrintUtil.println("注意填写规范"));
        }
        responsePacket.setUserFriend(reMapper(userFriend));
        return responsePacket;
    }

    /**
     * 被申请人 同意申请后，需要通知申请人已通过验证，需要重新组装被申请人的信息 -> 好友信息
     * @param userFriend
     * @return
     */
    private UserFriend reMapper(UserFriend userFriend){
        UserFriend uf = new UserFriend();
        int userFriendId = userFriend.getUserFriendId();
        int userId = userFriend.getUserId();
        String userFriendName = userFriend.getUserFriendName();
        String userName = userFriend.getUserName();
        uf.setUserFriendId(userId);
        uf.setUserId(userFriendId);
        uf.setUserName(userFriendName);
        uf.setUserFriendName(userName);
        uf.setStatus(UserStatus.ONLINE.status);
        return uf;
    }

    /**
     * 被申请人 同意申请后，需要组装申请人信息 -> 好友信息, 存入session和h2
     * @param approvalNotice
     * @return
     */
    private UserFriend mapper(UserNotice approvalNotice) {
        UserFriend userFriend = new UserFriend();
        userFriend.setUserId(approvalNotice.getNoticeid());
        userFriend.setUserName(approvalNotice.getNoticename());
        userFriend.setUserFriendId(approvalNotice.getUserid());
        userFriend.setUserFriendName(approvalNotice.getUsername());
        /**
         * 判断对方是否在线
         */
        if (SessionUtil.getUser(approvalNotice.getUserid()) == null) {
            userFriend.setStatus(UserStatus.OFFLINE.status);
        }else {
            userFriend.setStatus(UserStatus.ONLINE.status);
        }
        return userFriend;
    }

}
