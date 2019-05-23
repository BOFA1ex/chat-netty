package com.bofa.server.handler;

import com.bofa.protocol.request.FriendCRequestPacket;
import com.bofa.server.service.UserFriendSv;
import com.bofa.server.util.HttpUtil;
import com.bofa.server.util.TaskManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.server.handler
 * @date 2019/5/6
 */
@ChannelHandler.Sharable
public class FriendCRequestHandler extends SimpleChannelInboundHandler<FriendCRequestPacket> {

    static final FriendCRequestHandler INSTANCE = new FriendCRequestHandler();
    static final Logger logger = LoggerFactory.getLogger(FriendCRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FriendCRequestPacket requestPacket) throws Exception {
        TaskManager.execute("friend change status", () -> UserFriendSv.changeStatus(requestPacket));
    }
}
