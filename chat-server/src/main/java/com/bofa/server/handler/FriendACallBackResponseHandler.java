package com.bofa.server.handler;

import com.bofa.attribute.ApprovalStatus;
import com.bofa.attribute.NoticeStatus;
import com.bofa.attribute.NoticeType;
import com.bofa.entity.User;
import com.bofa.entity.UserFriend;
import com.bofa.entity.UserNotice;
import com.bofa.protocol.request.FriendARequestPacket;
import com.bofa.protocol.response.FriendACallBackResponsePacket;
import com.bofa.protocol.response.NoticeResponsePacket;
import com.bofa.server.service.UserFriendSv;
import com.bofa.server.service.UserNoticeSv;
import com.bofa.server.util.TaskManager;
import com.bofa.util.LocalDateTimeUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.server.handler
 * @date 2019/4/28
 */
@ChannelHandler.Sharable
public class FriendACallBackResponseHandler extends SimpleChannelInboundHandler<FriendACallBackResponsePacket> {

    static final FriendACallBackResponseHandler INSTANCE = new FriendACallBackResponseHandler();

    static final Logger logger = LoggerFactory.getLogger(FriendACallBackResponseHandler.class);

    /**
     * receive the noticed-client callback-message
     * when toUser is offline, save approval-callback notice and update approval status SC or FA.
     * when toUser is online, writeAndFlush to the channel to noticed it and don't forget update approval status.
     *
     * @param ctx
     * @param responsePacket
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FriendACallBackResponsePacket responsePacket) throws Exception {
        UserNotice userNotice = responsePacket.getUserNotice();
        UserFriend userFriend = responsePacket.getUserFriend();
        User toUser = SessionUtil.getUser(userNotice.getUserid());
        String topic = "[" + userNotice.getNoticename() + "] approval callBack";
        FriendARequestPacket result = mapper(responsePacket);
        if (responsePacket.isSuccess()) {
            userNotice.setNoticecontent(userNotice.getNoticename() + "通过了您的好友验证");
            TaskManager.topicExecute(topic, "save userFriend", () ->
                    UserFriendSv.saveUserFriend(responsePacket.getUserFriend()), false);
        } else {
            userNotice.setNoticecontent(userNotice.getNoticename() + "拒绝好友验证: " + responsePacket.getFailReason());
        }
        final UserNotice finalUserNotice = mapper(userNotice);
        if (toUser == null) {
            TaskManager.topicExecute(topic, "save notice [approval callback]", () ->
                    UserNoticeSv.saveNotice(finalUserNotice), false);
        } else {
            TaskManager.topicExecute(topic, "notice approver", () -> {
                SessionUtil.getChannel(toUser.getUserId())
                        .writeAndFlush(new NoticeResponsePacket(finalUserNotice, userFriend));
            }, false);
        }
        TaskManager.topicExecute(topic, "update approval",
                () -> UserFriendSv.updateApproval(result), true);
    }

    private UserNotice mapper(UserNotice userNotice) {
        Integer noticeId = userNotice.getUserid();
        String noticeName = userNotice.getUsername();
        userNotice.setNoticedatetime(LocalDateTimeUtil.now0());
        userNotice.setNoticestatus(NoticeStatus.UNREAD.status);
        userNotice.setNoticetype(NoticeType.FRIEND_APPROVAL_MESSAGE_CALLBACK.type);
        userNotice.setUserid(userNotice.getNoticeid());
        userNotice.setUsername(userNotice.getNoticename());
        userNotice.setNoticeid(noticeId);
        userNotice.setNoticename(noticeName);
        return userNotice;
    }

    private FriendARequestPacket mapper(FriendACallBackResponsePacket responsePacket) {
        UserNotice notice = responsePacket.getUserNotice();
        FriendARequestPacket requestPacket = new FriendARequestPacket();
        requestPacket.setApprovaluserid(notice.getUserid());
        requestPacket.setApprovalusername(notice.getUsername());
        requestPacket.setApprovalcontent(notice.getNoticecontent());
        requestPacket.setApprovaldatetime(notice.getNoticedatetime());
        requestPacket.setApprovaltouserid(notice.getNoticeid());
        requestPacket.setApprovaltousername(notice.getNoticename());
        if (responsePacket.isSuccess()) {
            requestPacket.setStatus(ApprovalStatus.APPLY_SC);
        } else {
            requestPacket.setStatus(ApprovalStatus.APPLY_FA);
            requestPacket.setApprovalfailreason(responsePacket.getFailReason());
        }
        return requestPacket;
    }
}
