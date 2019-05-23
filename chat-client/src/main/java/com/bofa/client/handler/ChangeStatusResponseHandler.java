package com.bofa.client.handler;

import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.entity.User;
import com.bofa.protocol.response.ChangeStatusResponsePacket;
import com.bofa.util.PrintUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.handler
 * @date 2019/4/13
 */
@ChannelHandler.Sharable
public class ChangeStatusResponseHandler extends SimpleChannelInboundHandler<ChangeStatusResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChangeStatusResponsePacket responsePacket) throws Exception {
        User user = SessionUtil.getSession(ctx.channel()).getUser();
        if (responsePacket.isSuccess()) {
            user.setStatus(responsePacket.getStatus());
        } else {
            PrintStreamDelegate.delegate(failAction(user.getUserName(), responsePacket.getMessage()));
        }
    }

    private static Runnable failAction(String userName, String message){
        return () -> PrintUtil.println(userName, message);
    }
}
