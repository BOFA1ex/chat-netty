package com.bofa.client.handler;

import com.bofa.entity.MessageInfo;
import com.bofa.protocol.request.MessageCallBackRequestPacket;
import com.bofa.protocol.response.MessageResponsePacket;
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
public class MessageResponseHandler extends SimpleChannelInboundHandler<MessageResponsePacket> {

    static final MessageResponseHandler INSTANCE = new MessageResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageResponsePacket responsePacket) throws Exception {
        MessageInfo messageInfo = responsePacket.getMessageInfo();
        PrintUtil.println(responsePacket.getFromUserName()," -> " + messageInfo.getContent());
        /**
         * denote client get the message which the other client sent.
         */
        ctx.writeAndFlush(new MessageCallBackRequestPacket(
                messageInfo.getFromUserId(), messageInfo.getToUserId(), messageInfo.getContent()));
    }
}
