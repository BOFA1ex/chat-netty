package com.bofa.client.handler;

import com.bofa.client.util.PrintUtil;
import com.bofa.entity.User;
import com.bofa.protocol.response.RegisterResponsePacket;
import com.bofa.session.Session;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.handler
 * @date 2019/4/8
 */
@ChannelHandler.Sharable
public class RegisterResponseHandler extends SimpleChannelInboundHandler<RegisterResponsePacket> {

    public static final RegisterResponseHandler INSTANCE = new RegisterResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterResponsePacket response) throws Exception {
        User user = response.getUser();
        String userName = user.getUserName();
        if (response.isSuccess()) {
            PrintUtil.println(userName, "register and login success");
            SessionUtil.bindSession(new Session(user, response.getUserFriends()), ctx.channel());
        } else {
            PrintUtil.println(userName, "register fail, reason: " + response.getMessage());
        }
    }
}
