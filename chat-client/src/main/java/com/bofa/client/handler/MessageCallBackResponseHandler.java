package com.bofa.client.handler;

import com.bofa.protocol.response.MessageCallBackResponsePacket;
import com.bofa.util.PrintUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.handler
 * @date 2019/4/13
 */
public class MessageCallBackResponseHandler extends SimpleChannelInboundHandler<MessageCallBackResponsePacket> {

    static final MessageCallBackResponseHandler INSTANCE = new MessageCallBackResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageCallBackResponsePacket responsePacket) throws Exception {
        if (responsePacket.isSuccess()) {
            PrintUtil.println(responsePacket.getToUserName(), "read your message [" + responsePacket.getContent() + "]");
        } else {
            PrintUtil.println(responsePacket.getToUserName(), responsePacket.getMessage());
        }
    }
}
