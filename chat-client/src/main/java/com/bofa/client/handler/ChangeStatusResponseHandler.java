package com.bofa.client.handler;

import com.bofa.client.util.PrintUtil;
import com.bofa.protocol.response.ChangeStatusResponsePacket;
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
        String userName = responsePacket.getUserName();
        if (responsePacket.isSuccess()) {
            PrintUtil.println(userName, responsePacket.getMessage());
        }else {
            PrintUtil.println(userName, responsePacket.getMessage());
        }
    }
}
