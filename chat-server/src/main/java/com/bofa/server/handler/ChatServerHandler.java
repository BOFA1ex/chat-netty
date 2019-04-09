package com.bofa.server.handler;

import com.bofa.protocol.command.Command;
import com.bofa.protocol.request.AbstractRequestPacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.handler
 * @date 2019/4/8
 */
@ChannelHandler.Sharable
public class ChatServerHandler extends SimpleChannelInboundHandler<AbstractRequestPacket> {

    public static final ChatServerHandler INSTANCE = new ChatServerHandler();

    private static final Map<Byte, SimpleChannelInboundHandler<? extends AbstractRequestPacket>> handleMap;

    static {
        handleMap = new HashMap<>();
        handleMap.put(Command.LOGIN_REQUEST.command, LoginRequestHandler.INSTANCE);
        handleMap.put(Command.REGISTER_REQUEST.command, RegisterRequestHandler.INSTANCE);
        handleMap.put(Command.LOGOUT_REQUEST.command, LogoutRequestHandler.INSTANCE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractRequestPacket packet) throws Exception {
        handleMap.get(packet.getCommand()).channelRead(ctx, packet);
    }
}
