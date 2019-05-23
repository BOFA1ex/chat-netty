package com.bofa.client.handler;

import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.protocol.response.LogoutResponsePacket;
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
 * @date 2019/4/9
 */

@ChannelHandler.Sharable
public class LogoutResponseHandler extends SimpleChannelInboundHandler<LogoutResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogoutResponsePacket logoutResponsePacket) throws Exception {
        String userName = logoutResponsePacket.getUserName();
        if (logoutResponsePacket.isSuccess()) {
            PrintStreamDelegate.delegate(successAction(userName));
            SessionUtil.unbindSession(ctx.channel());
        } else {
            PrintStreamDelegate.delegate(failAction(userName, logoutResponsePacket.getMessage()));
        }
        SessionUtil.signalRespOrder();
    }

    private static Runnable successAction(String userName) {
        return () -> PrintUtil.println(userName, "logout success");
    }

    private static Runnable failAction(String userName, String message) {
        return () -> PrintUtil.println(userName, "logout fail: " + message);
    }
}
