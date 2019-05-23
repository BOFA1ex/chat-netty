package com.bofa.client.handler;

import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.protocol.response.FriendAResponsePacket;
import com.bofa.session.Session;
import com.bofa.util.PrintUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.handler
 * @date 2019/4/27
 */
@ChannelHandler.Sharable
public class FriendAResponseHandler extends SimpleChannelInboundHandler<FriendAResponsePacket> {

    /**
     * 好友申请响应处理需要阻塞等待(是否查有此人)
     * @param ctx
     * @param response
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FriendAResponsePacket response) throws Exception {
        if (response.isSuccess()) {
            PrintStreamDelegate.delegate(successAction(response.getToUserName()));
        } else {
            PrintStreamDelegate.delegate(failAction(response.getCode(), response.getMessage()));
        }
        SessionUtil.signalRespOrder();
    }

    private static Runnable successAction(String toUserName) {
        return () -> PrintUtil.println(toUserName, "approval success");
    }

    private static Runnable failAction(String toUserName, String message) {
        return () -> PrintUtil.println(toUserName, "approval fail, reason: " + message);
    }
}
