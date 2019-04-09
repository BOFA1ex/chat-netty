package com.bofa.client.handler;

import com.bofa.protocol.response.LoginResponsePacket;
import com.bofa.session.Session;
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
        String userName = loginResponsePacket.getUsername();
        Integer userId = loginResponsePacket.getUserid();
//        try{
        if (loginResponsePacket.isSuccess()) {
            System.out.println("[" + userName + "]" + "login success");
            SessionUtil.bindSession(new Session(userId, userName), ctx.channel());
        } else {
            System.out.println("[" + userName + "]" + "login fail, reason: " + loginResponsePacket.getMessage());
        }
//        }finally{
//            SessionUtil.signalLoginOrder();
//    }
    }
}
