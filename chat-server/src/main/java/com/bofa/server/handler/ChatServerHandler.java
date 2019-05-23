package com.bofa.server.handler;

import com.bofa.attribute.UserStatus;
import com.bofa.entity.User;
import com.bofa.protocol.Packet;
import com.bofa.protocol.command.Command;
import com.bofa.protocol.request.AbstractRequestPacket;
import com.bofa.protocol.request.LogoutRequestPacket;
import com.bofa.server.service.UserSv;
import com.bofa.server.util.LoggerUtil;
import com.bofa.server.util.TaskManager;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.server.handler
 * @date 2019/4/8
 */
@ChannelHandler.Sharable
public class ChatServerHandler extends SimpleChannelInboundHandler<Packet> {

    public static final ChatServerHandler INSTANCE = new ChatServerHandler();

    static final Logger logger = LoggerFactory.getLogger(ChatServerHandler.class);

    private static final Map<Byte, SimpleChannelInboundHandler> handleMap;

    static {
        handleMap = new HashMap<>();
        handleMap.put(Command.REGISTER_REQUEST.command, RegisterRequestHandler.INSTANCE);
        handleMap.put(Command.LOGOUT_REQUEST.command, LogoutRequestHandler.INSTANCE);
        handleMap.put(Command.MESSAGE_REQUEST.command, MessageRequestHandler.INSTANCE);
        handleMap.put(Command.MESSAGE_CALLBACK_REQUEST.command, MessageCallBackRequestHandler.INSTANCE);
        handleMap.put(Command.CHANGE_STATUS_REQUEST.command, ChangeStatusRequestHandler.INSTANCE);
        handleMap.put(Command.FRIENDA_REQUEST.command, FriendARequestHandler.INSTANCE);
        handleMap.put(Command.FRIENDA_CALLBACK_RESPONSE.command, FriendACallBackResponseHandler.INSTANCE);
        handleMap.put(Command.FRIENDC_REQUEST.command, FriendCRequestHandler.INSTANCE);
        handleMap.put(Command.CLIENT_CLOSE.command, ClientCloseRequestHandler.INSTANCE);
        handleMap.values().forEach(System.out::println);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        handleMap.get(packet.getCommand()).channelRead(ctx, packet);
    }

}
