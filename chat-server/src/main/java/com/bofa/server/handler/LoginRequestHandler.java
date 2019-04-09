package com.bofa.server.handler;

import com.bofa.protocol.request.LoginRequestPacket;
import com.bofa.protocol.response.LoginResponsePacket;
import com.bofa.server.service.UserSv;
import com.bofa.session.Session;
import com.bofa.util.LocalDateTimeUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.handler
 * @date 2019/4/3
 */
@ChannelHandler.Sharable
public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {

    static final LoginRequestHandler INSTANCE = new LoginRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestPacket loginRequestPacket) throws Exception {
        LoginResponsePacket response = UserSv.login(loginRequestPacket);
        if (response.isSuccess()) {
            System.out.println("[" + response.getUsername() + "] login success");
            SessionUtil.bindSession(new Session(response.getUserid(), response.getUsername()), ctx.channel());
        } else {
            System.out.println("[" + response.getUsername() + "] login fail");
        }
        ctx.channel().writeAndFlush(response);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (SessionUtil.hasLogin(ctx.channel())) {
            SessionUtil.unbindSession(ctx.channel());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = ((NioSocketChannel) ctx.channel()).remoteAddress();
        System.err.println(LocalDateTimeUtil.now() + " "
                + address.getHostString() + ":" + address.getPort() + " linked");
        super.channelActive(ctx);
    }


}
