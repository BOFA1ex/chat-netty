package com.bofa.client.handler;

import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.protocol.response.MessageCallBackResponsePacket;
import com.bofa.util.PrintUtil;
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
public class MessageCallBackResponseHandler extends SimpleChannelInboundHandler<MessageCallBackResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageCallBackResponsePacket responsePacket) throws Exception {
        if (responsePacket.isSuccess()) {
            PrintStreamDelegate.delegate(successAction(responsePacket.getToUserName(),responsePacket.getContent()));
        } else {
            PrintStreamDelegate.delegate(failAction(responsePacket.getToUserName(), responsePacket.getMessage()));
        }
    }

    private static Runnable successAction(String toUserName, String content) {
        return () -> PrintUtil.println(toUserName, "read your message [" + content + "]");
    }

    private static Runnable failAction(String userName, String message) {
        return () -> PrintUtil.println(userName, "logout fail: " + message);
    }
}
