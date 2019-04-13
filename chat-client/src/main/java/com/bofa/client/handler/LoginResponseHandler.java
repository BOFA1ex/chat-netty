package com.bofa.client.handler;

import com.bofa.client.util.PrintUtil;
import com.bofa.entity.User;
import com.bofa.protocol.response.LoginResponsePacket;
import com.bofa.session.Session;
import com.bofa.util.LocalDateTimeUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.handler
 * @date 2019/4/2
 */
@ChannelHandler.Sharable
public class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponsePacket> {

    public static final LoginResponseHandler INSTANCE = new LoginResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponsePacket loginResponsePacket) throws Exception {
        User user = loginResponsePacket.getUser();
        if (loginResponsePacket.isSuccess()) {
            PrintUtil.println(user.getUserName(), "login success");
            SessionUtil.bindSession(new Session(user, loginResponsePacket.getUserFriends()), ctx.channel());
        } else {
            PrintUtil.println(loginResponsePacket.getCode(), "login fail, reason: " + loginResponsePacket.getMessage());
        }
        SessionUtil.signalRespOrder();
    }
}
