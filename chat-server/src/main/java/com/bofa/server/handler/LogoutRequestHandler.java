package com.bofa.server.handler;

import com.bofa.exception.ChatException;
import com.bofa.protocol.request.LogoutRequestPacket;
import com.bofa.protocol.response.LogoutResponsePacket;
import com.bofa.server.service.UserSv;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.handler
 * @date 2019/4/9
 */
public class LogoutRequestHandler extends SimpleChannelInboundHandler<LogoutRequestPacket> {

    public static LogoutRequestHandler INSTANCE = new LogoutRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogoutRequestPacket requestPacket) throws Exception {
        LogoutResponsePacket response = UserSv.logout(requestPacket);
        response.setUsername(SessionUtil.getSession(ctx.channel()).getUserName());
        if (response.isSuccess()) {
            SessionUtil.unbindSession(ctx.channel());
        } else {
            System.out.println(response.getMessage());
        }
        ctx.channel().writeAndFlush(response);
    }
}
