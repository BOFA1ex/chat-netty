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
        String toUserName = userMessage.getTousername();
        User toUser = SessionUtil.getUser(userMessage.getTouserid());
        /**
         * 判断好友是否在线
         * 离线或者隐身需要把消息存入通知信息表(新增通知信息，新增用户离线信息), 并响应客户端(好友不在线)
         * 在线则根据Session获取channel，发送信息给另一个客户端，等待该客户端的回调响应
         */
        TaskManager.execute("save message", () -> UserMessageSv.message(requestPacket));
        if (toUser == null) {
            TaskManager.execute("callback offline", () -> {
                MessageCallBackResponsePacket responsePacket = new MessageCallBackResponsePacket("不在线", toUserName);
                responsePacket.setSuccess(false);
                responsePacket.setMessage("不在线");
                ctx.channel().writeAndFlush(responsePacket);
            });
            TaskManager.execute("save notice [offline-message]", () -> {
                UserNotice notice = new UserNotice();
                notice.setNoticecontent(userMessage.getContent());
                notice.setNoticedatetime(userMessage.getDatetime());
                notice.setUserid(userMessage.getFromuserid());
                notice.setNoticeid(userMessage.getTouserid());
                notice.setNoticestatus(NoticeStatus.UNREAD.status);
                notice.setNoticetype(NoticeType.FRIEND_OFFLINE_MESSAGE.type);
                return UserMessageSv.offlineMessage(notice);
            });
            return;
        } else if (toUser.getStatus() == UserStatus.VISIBLE.status) {
            TaskManager.execute("callback visible", () -> {
                MessageCallBackResponsePacket responsePacket = new MessageCallBackResponsePacket("不在线", toUserName);
                responsePacket.setSuccess(false);
                responsePacket.setMessage("不在线");
                ctx.channel().writeAndFlush(responsePacket);
            });
        }

        TaskManager.execute("send online-message", () -> {
            MessageResponsePacket responsePacket = new MessageResponsePacket();
            responsePacket.setFromUserName(userMessage.getFromusername());
            responsePacket.setUserMessage(userMessage);
            LoggerUtil.info(logger, userMessage.getFromusername(), "send message -> [" + userMessage.getTousername() + "] " + userMessage.getContent());
            SessionUtil.getChannel(userMessage.getTouserid()).writeAndFlush(responsePacket);
        });
    }

}
