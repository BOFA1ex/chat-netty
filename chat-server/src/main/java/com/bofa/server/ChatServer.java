package com.bofa.server;

import com.bofa.codeC.PacketCodeHandler;
import com.bofa.codeC.PacketDecoder;
import com.bofa.codeC.PacketEncoder;
import com.bofa.codeC.Spliter;
import com.bofa.server.handler.AuthHandler;
import com.bofa.server.handler.ChatServerHandler;
import com.bofa.server.handler.LoginRequestHandler;
import com.bofa.server.handler.RegisterRequestHandler;
import com.bofa.util.LocalDateTimeUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.time.LocalDateTime;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server
 * @date 2019/4/2
 */
public class ChatServer {

    private static final int DEFAULT_PORT = 8000;

    public static void main(String[] args) {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new Spliter());
                        ch.pipeline().addLast(PacketCodeHandler.INSTANCE);
                        ch.pipeline().addLast(ChatServerHandler.INSTANCE);
                        ch.pipeline().addLast(AuthHandler.INSTANCE);
                    }
                });
        bind(serverBootstrap, DEFAULT_PORT);
    }

    static void bind(ServerBootstrap serverBootstrap, int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.err.println(LocalDateTimeUtil.now() + " port[" + port + "] bind success");
            } else {
                bind(serverBootstrap, port + 1);
            }
        });
    }
}
