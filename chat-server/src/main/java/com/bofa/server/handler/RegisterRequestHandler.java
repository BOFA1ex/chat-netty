package com.bofa.server.handler;

import com.bofa.protocol.request.RegisterRequestPacket;
import com.bofa.protocol.response.RegisterResponsePacket;
import com.bofa.server.service.UserSv;
import com.bofa.session.Session;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.handler
 * @date 2019/4/8
 */
@ChannelHandler.Sharable
public class RegisterRequestHandler extends SimpleChannelInboundHandler<RegisterRequestPacket>{

    static final RegisterRequestHandler INSTANCE = new RegisterRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterRequestPacket requestPacket) throws Exception {
        RegisterResponsePacket response = UserSv.register(requestPacket);
        if (response.isSuccess()) {
            System.out.println("[" + requestPacket.getUsername() + "] register success");
            SessionUtil.bindSession(new Session(response.getUserid(), response.getUsername()), ctx.channel());
        } else {
            System.out.println("[" + requestPacket.getUsername() + "] register fail");
        }
        ctx.channel().writeAndFlush(response);
    }
}
