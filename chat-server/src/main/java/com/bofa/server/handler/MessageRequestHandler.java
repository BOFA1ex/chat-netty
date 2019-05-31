package com.bofa.server.handler;

import com.bofa.attribute.NoticeStatus;
import com.bofa.attribute.NoticeType;
import com.bofa.attribute.UserStatus;
import com.bofa.entity.User;
import com.bofa.entity.UserMessage;
import com.bofa.entity.UserNotice;
import com.bofa.protocol.request.MessageRequestPacket;
import com.bofa.protocol.response.MessageCallBackResponsePacket;
import com.bofa.protocol.response.MessageResponsePacket;
import com.bofa.server.service.UserMessageSv;
import com.bofa.server.util.LoggerUtil;
import com.bofa.server.util.TaskManager;
import com.bofa.session.Session;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.handler
 * @date 2019/4/13
 */
public class MessageRequestHandler extends SimpleChannelInboundHandler<MessageRequestPacket> {

    static final MessageRequestHandler INSTANCE = new MessageRequestHandler();

    static final Logger logger = LoggerFactory.getLogger(MessageRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageRequestPacket requestPacket) throws Exception {
        UserMessage userMessage = requestPacket.getUserMessage();
        String userName = userMessage.getFromusername();
        String toUserName = userMessage.getTousername();
        User toUser = SessionUtil.getUser(userMessage.getTouserid());
        String topic = "[" + userName + "] send message to [" + toUserName + "]";

        /**
         * toUser offline
         * topicExecute -> [save message] -> [save notice [offline-message]]
         * ps: topicExecute is make sure business data consistency
         * execute callback to remind the fromUser
         * toUser online
         * execute [save message]
         * execute [send online-message]
         */
        if (toUser == null) {
            TaskManager.topicExecute(topic, "save message", () -> UserMessageSv.message(requestPacket), ctx.channel(), false);
            TaskManager.execute("callback offline", () -> {
                MessageCallBackResponsePacket responsePacket = new MessageCallBackResponsePacket("不在线", toUserName, false);
                responsePacket.setSuccess(false);
                responsePacket.setMessage("不在线, 已发送离线信息");
                ctx.channel().writeAndFlush(responsePacket);
            });
            TaskManager.topicExecute(topic, "save notice [offline-message]", () -> {
                UserNotice notice = mapper(userMessage);
                return UserMessageSv.offlineMessage(notice);
            }, ctx.channel(), true);
            return;
        } else if (toUser.getStatus() == UserStatus.VISIBLE.status) {
            TaskManager.execute("callback visible", () -> {
                MessageCallBackResponsePacket responsePacket = new MessageCallBackResponsePacket("不在线", toUserName, false);
                responsePacket.setSuccess(false);
                responsePacket.setMessage("不在线, 已发送离线信息");
                ctx.channel().writeAndFlush(responsePacket);
            });
        }
        TaskManager.execute("save message", () -> UserMessageSv.message(requestPacket));
        TaskManager.execute("send online-message", () -> {
            MessageResponsePacket responsePacket = new MessageResponsePacket();
            responsePacket.setFromUserName(userMessage.getFromusername());
            responsePacket.setUserMessage(userMessage);
            LoggerUtil.info(logger, userMessage.getFromusername(), "send message -> [" + userMessage.getTousername() + "] " + userMessage.getContent());
            SessionUtil.getChannel(userMessage.getTouserid()).writeAndFlush(responsePacket);
        });
    }

    /**
     * when toUser is offline, save notice to remind it.
     * mapper userNotice by userMessage
     * @param userMessage
     * @return
     */
    private UserNotice mapper(UserMessage userMessage){
        UserNotice notice = new UserNotice();
        notice.setNoticecontent(userMessage.getContent());
        notice.setNoticedatetime(userMessage.getDatetime());
        notice.setUserid(userMessage.getFromuserid());
        notice.setUsername(userMessage.getFromusername());
        notice.setNoticeid(userMessage.getTouserid());
        notice.setNoticename(userMessage.getTousername());
        notice.setNoticestatus(NoticeStatus.UNREAD.status);
        notice.setNoticetype(NoticeType.FRIEND_UNREAD_MESSAGE.type);
        return notice;
    }

}
