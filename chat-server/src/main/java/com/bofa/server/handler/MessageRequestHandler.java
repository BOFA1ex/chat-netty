package com.bofa.server.handler;

import com.bofa.entity.MessageInfo;
import com.bofa.protocol.request.MessageRequestPacket;
import com.bofa.protocol.response.MessageResponsePacket;
import com.bofa.server.TaskManager;
import com.bofa.server.service.UserSv;
import com.bofa.server.util.LoggerUtil;
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
        TaskManager.execute("send message", () -> {
            MessageInfo messageInfo = requestPacket.getMessageInfo();
            MessageResponsePacket responsePacket = UserSv.message(requestPacket);
            Session s1 = SessionUtil.getSession(ctx.channel());
            Session s2 = SessionUtil.getSession(messageInfo.getToUserId());
            responsePacket.setFromUserName(s1.getUser().getUserName());
            LoggerUtil.info(logger, s1.getUser().getUserName(), "send message -> [" + s2.getUser().getUserName() + "] " + messageInfo.getContent());
            SessionUtil.getChannel(messageInfo.getToUserId()).writeAndFlush(responsePacket);
        });
    }
}
