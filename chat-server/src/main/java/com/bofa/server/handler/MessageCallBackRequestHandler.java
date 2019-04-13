package com.bofa.server.handler;

import com.bofa.protocol.request.MessageCallBackRequestPacket;
import com.bofa.protocol.response.MessageCallBackResponsePacket;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.handler
 * @date 2019/4/13
 */
public class MessageCallBackRequestHandler extends SimpleChannelInboundHandler<MessageCallBackRequestPacket> {

    static final MessageCallBackRequestHandler INSTANCE = new MessageCallBackRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageCallBackRequestPacket requestPacket) throws Exception {
        String userName = SessionUtil.getSession(ctx.channel()).getUser().getUserName();
        SessionUtil.getChannel(requestPacket.getFromUserId()).writeAndFlush(new MessageCallBackResponsePacket(requestPacket.getContent(), userName));
    }
}
