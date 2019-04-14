package com.bofa.client.handler;

import com.bofa.entity.User;
import com.bofa.protocol.response.ChangeStatusResponsePacket;
import com.bofa.util.PrintUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.handler
 * @date 2019/4/13
 */
public class ChangeStatusResponseHandler extends SimpleChannelInboundHandler<ChangeStatusResponsePacket> {

    static final ChangeStatusResponseHandler INSTANCE = new ChangeStatusResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChangeStatusResponsePacket responsePacket) throws Exception {
        User user = SessionUtil.getSession(ctx.channel()).getUser();
        if (responsePacket.isSuccess()) {
            user.setStatus(responsePacket.getStatus());
        } else {
            PrintUtil.println(user.getUserName(), responsePacket.getMessage());
        }
    }
}
