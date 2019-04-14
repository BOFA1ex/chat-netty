package com.bofa.server.handler;

import com.bofa.entity.MessageInfo;
import com.bofa.protocol.request.MessageRequestPacket;
import com.bofa.protocol.response.MessageCallBackResponsePacket;
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
        MessageInfo messageInfo = requestPacket.getMessageInfo();
        String toUserName = messageInfo.getToUserName();
        Session s1 = SessionUtil.getSession(ctx.channel());
        Session s2 = SessionUtil.getSession(messageInfo.getToUserId());
        /**
         * 判断好友是否在线
         * 离线需要把消息存入通知信息表(新增通知信息，新增用户离线信息), 并响应客户端(好友不在线)
         * 在线则根据Session获取channel，发送信息给另一个客户端，等待该客户端的回调响应
         */
        if (s2 == null) {
            MessageCallBackResponsePacket responsePacket = new MessageCallBackResponsePacket("不在线", toUserName);
            responsePacket.setSuccess(false);
            responsePacket.setMessage("不在线");
            ctx.channel().writeAndFlush(responsePacket);
            return;
        }
        TaskManager.execute("send online-message", () -> {
            MessageResponsePacket responsePacket = new MessageResponsePacket();
            responsePacket.setFromUserName(s1.getUser().getUserName());
            responsePacket.setMessageInfo(requestPacket.getMessageInfo());
            LoggerUtil.info(logger, s1.getUser().getUserName(), "send message -> [" + s2.getUser().getUserName() + "] " + messageInfo.getContent());
            SessionUtil.getChannel(messageInfo.getToUserId()).writeAndFlush(responsePacket);
            boolean flag = UserSv.message(requestPacket).isSuccess();
            while (!flag) {
                flag = UserSv.message(requestPacket).isSuccess();
            }
        });
    }
}
