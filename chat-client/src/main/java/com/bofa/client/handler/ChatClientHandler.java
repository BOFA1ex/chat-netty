package com.bofa.client.handler;

import com.bofa.client.console.ClientCommand;
import com.bofa.protocol.command.Command;
import com.bofa.protocol.response.AbstractResponsePacket;
import com.bofa.protocol.response.ChangeStatusResponsePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.handler
 * @date 2019/4/8
 */
@ChannelHandler.Sharable
public class ChatClientHandler extends SimpleChannelInboundHandler<AbstractResponsePacket> {

    public static final ChatClientHandler INSTANCE = new ChatClientHandler();

    private static final Map<Byte, SimpleChannelInboundHandler<? extends AbstractResponsePacket>> handlerMap;

    static {
        handlerMap = new HashMap<>();
        handlerMap.put(Command.LOGIN_RESPONSE.command, LoginResponseHandler.INSTANCE);
        handlerMap.put(Command.REGISTER_RESPONSE.command, RegisterResponseHandler.INSTANCE);
        handlerMap.put(Command.LOGOUT_RESPONSE.command, LogoutResponseHandler.INSTANCE);
        handlerMap.put(Command.MESSAGE_RESPONSE.command, MessageResponseHandler.INSTANCE);
        handlerMap.put(Command.MESSAGE_CALLBACK_RESPONSE.command, MessageCallBackResponseHandler.INSTANCE);
        handlerMap.put(Command.CHANGE_STATUS_RESPONSE.command, ChangeStatusResponseHandler.INSTANCE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractResponsePacket packet) throws Exception {
        handlerMap.get(packet.getCommand()).channelRead(ctx, packet);
    }
}
