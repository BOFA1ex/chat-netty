package com.bofa.server.handler;

import com.bofa.attribute.NoticeStatus;
import com.bofa.attribute.NoticeType;
import com.bofa.attribute.UserStatus;
import com.bofa.entity.User;
import com.bofa.entity.UserNotice;
import com.bofa.protocol.request.FriendACallBackRequestPacket;
import com.bofa.protocol.request.FriendARequestPacket;
import com.bofa.protocol.response.AbstractResponsePacket;
import com.bofa.protocol.response.FriendAResponsePacket;
import com.bofa.protocol.response.NoticeResponsePacket;
import com.bofa.server.service.UserFriendSv;
import com.bofa.server.service.UserNoticeSv;
import com.bofa.server.util.TaskManager;
import com.bofa.session.Session;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.server.handler
 * @date 2019/4/27
 */
@ChannelHandler.Sharable
public class FriendARequestHandler extends SimpleChannelInboundHandler<FriendARequestPacket> {

    static final FriendARequestHandler INSTANCE = new FriendARequestHandler();
    static final Logger logger = LoggerFactory.getLogger(FriendARequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FriendARequestPacket requestPacket) throws Exception {
        Integer toUserId = requestPacket.getApprovaltouserid();
        String toUserName = requestPacket.getApprovaltousername();
        String userName = requestPacket.getApprovalusername();
        String topic = "[" + userName + "] approval friend to [" + toUserName + "]";
        User toUser = null;
        if (toUserId != null) {
            toUser = SessionUtil.getUser(toUserId);
        }
        if (StringUtils.isNotEmpty(toUserName)) {
            toUser = SessionUtil.getUser(toUserName);
        }
        TaskManager.topicExecute(topic, "save friend approval",
                () -> UserFriendSv.submitApproval(requestPacket),
                response -> {
                    FriendAResponsePacket responsePacket = null;
                    try {
                        responsePacket = (FriendAResponsePacket) response;
                        responsePacket.setToUserName(requestPacket.getApprovaltousername());
                    } finally {
                        ctx.channel().writeAndFlush(responsePacket);
                    }
                }, ctx.channel(), false);
        /**
         * 如果申请对象离线，则存入通知
         * 反之直接通知对方
         */
        UserNotice approvalNotice = mapper(requestPacket);
        if (toUser == null || toUser.getStatus() == UserStatus.VISIBLE.status) {
            TaskManager.topicExecute(topic, "save approval notice",
                    () -> UserNoticeSv.saveNotice(approvalNotice), ctx.channel(), true);
        } else {
            final int approval2userId = toUser.getUserId();
            TaskManager.topicExecute(topic, "send approval online", () -> {
                FriendACallBackRequestPacket packet = new FriendACallBackRequestPacket(approvalNotice);
                SessionUtil.getChannel(approval2userId).writeAndFlush(packet);
            }, ctx.channel(), true);
        }
    }

    private UserNotice mapper(FriendARequestPacket requestPacket) {
        UserNotice approvalNotice = new UserNotice();
        approvalNotice.setUserid(requestPacket.getApprovaluserid());
        approvalNotice.setUsername(requestPacket.getApprovalusername());
        approvalNotice.setNoticeid(requestPacket.getApprovaltouserid());
        approvalNotice.setNoticename(requestPacket.getApprovaltousername());
        approvalNotice.setNoticedatetime(requestPacket.getApprovaldatetime());
        approvalNotice.setNoticecontent(requestPacket.getApprovalcontent());
        approvalNotice.setNoticestatus(NoticeStatus.UNREAD.status);
        approvalNotice.setNoticetype(NoticeType.FRIEND_APPROVAL_MESSAGE.type);
        return approvalNotice;
    }
}
