package com.bofa.client.handler;

import com.bofa.protocol.response.LogoutResponsePacket;
import com.bofa.util.PrintUtil;
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
        String userName = logoutResponsePacket.getUserName();
        if (logoutResponsePacket.isSuccess()) {
            PrintUtil.println(userName, "logout success");
            SessionUtil.unbindSession(ctx.channel());
        } else {
            PrintUtil.println(userName, "logout fail: " + logoutResponsePacket.getMessage());
        }
        SessionUtil.signalRespOrder();
    }
}
