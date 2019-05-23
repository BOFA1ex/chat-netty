package com.bofa.client.handler;

import com.bofa.client.service.UserSv;
import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.entity.User;
import com.bofa.protocol.response.RegisterResponsePacket;
import com.bofa.session.Session;
import com.bofa.util.PrintUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.handler
 * @date 2019/4/8
 */
@ChannelHandler.Sharable
public class RegisterResponseHandler extends SimpleChannelInboundHandler<RegisterResponsePacket> {

    @Autowired
    private UserSv userSv;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterResponsePacket response) throws Exception {
        User user = response.getUser();
        if (response.isSuccess()) {
            PrintStreamDelegate.delegate(successAction(user.getUserName()));
            SessionUtil.bindSession(new Session(user, response.getUserFriends()), ctx.channel());
        } else {
            PrintStreamDelegate.delegate(failAction(response.getCode(), response.getMessage()));
        }
        SessionUtil.signalRespOrder();
    }

    private static Runnable successAction(String userName) {
        return () -> PrintUtil.println(userName, "register and login success");
    }

    private static Runnable failAction(String code, String message) {
        return () -> PrintUtil.println(code, "register fail, reason: " + message);
    }
}
