package com.bofa.server.handler;

import com.bofa.protocol.request.AbstractRequestPacket;
import com.bofa.protocol.request.ClientCloseRequestPacket;
import com.bofa.server.util.LoggerUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.server.handler
 * @date 2019/5/23
 */
@ChannelHandler.Sharable
public class ClientCloseRequestHandler extends SimpleChannelInboundHandler<ClientCloseRequestPacket> {

    static final ClientCloseRequestHandler INSTANCE = new ClientCloseRequestHandler();

    static final Logger logger = LoggerFactory.getLogger(ClientCloseRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientCloseRequestPacket msg) throws Exception {
        Channel channel = ctx.channel();
        InetSocketAddress address = (InetSocketAddress) channel.remoteAddress();
        channel.closeFuture().addListener(future -> {
            Optional.ofNullable(future.cause()).ifPresent(Throwable::printStackTrace);
            if (future.isSuccess()) {
                LoggerUtil.debug(logger, "CLIENT CLOSE", address.getHostString() + ":" + address.getPort());
            }
        });
    }
}
