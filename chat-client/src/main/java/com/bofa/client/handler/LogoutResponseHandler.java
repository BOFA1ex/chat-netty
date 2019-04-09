package com.bofa.client.handler;

import com.bofa.exception.ChatException;
import com.bofa.protocol.response.LogoutResponsePacket;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.handler
 * @date 2019/4/9
 */
public class LogoutResponseHandler extends SimpleChannelInboundHandler<LogoutResponsePacket> {

    public static final LogoutResponseHandler INSTANCE = new LogoutResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogoutResponsePacket logoutResponsePacket) throws Exception {
        if (logoutResponsePacket.isSuccess()) {
            System.out.println("[" + logoutResponsePacket.getUsername() + "]" + "logout success");
            SessionUtil.unbindSession(ctx.channel());
        } else {
            System.out.println("[" + logoutResponsePacket.getUsername() + "]" + "logout fail: " + logoutResponsePacket.getMessage());
        }
    }
}
