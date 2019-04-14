package com.bofa.client;

import com.bofa.client.console.ConsoleCommandManager;
import com.bofa.client.handler.ChatClientHandler;
import com.bofa.client.handler.LoginResponseHandler;
import com.bofa.client.handler.RegisterResponseHandler;
import com.bofa.codeC.PacketCodeHandler;
import com.bofa.codeC.PacketDecoder;
import com.bofa.codeC.PacketEncoder;
import com.bofa.codeC.Spliter;
import com.bofa.exception.ChatErrorCode;
import com.bofa.exception.ChatException;
import com.bofa.util.LocalDateTimeUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;
import java.util.concurrent.*;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client
 * @date 2019/4/2
 */
public class ChatClient {

    private static final int MAX_RETRY = 5;
    private static final int ARG_LENGTH = 2;
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 8000;
    private static final ThreadPoolExecutor SINGLE_CONSOLE_POOL;

    static {
        SINGLE_CONSOLE_POOL = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                new ThreadFactoryBuilder().setNameFormat("console-worker").build());
    }

    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        if (args.length > 0 && args.length != ARG_LENGTH) {
            System.out.println("args[0] must input host," +
                    " args[1] must input port");
        } else if (args.length == ARG_LENGTH) {
            host = args[0];
            port = Integer.valueOf(args[1]);
        }
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        final Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(workGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 6000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new Spliter());
                        ch.pipeline().addLast(PacketCodeHandler.INSTANCE);
                        ch.pipeline().addLast(ChatClientHandler.INSTANCE);
                    }
                });
        connect(bootstrap, host, port, MAX_RETRY);
    }

    static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                Channel channel = ((ChannelFuture) future).channel();
                System.out.println(LocalDateTimeUtil.now() + ": 连接成功");
                startConsoleThread(channel);
            } else if (retry == 0) {
                System.err.println("retry is empty");
            } else {
                int order = MAX_RETRY - retry + 1;
                int delay = 1 << order;
                System.err.println(LocalDateTimeUtil.now() + ": 连接失败, 第" + order + "次重新连接");
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1)
                        , delay, TimeUnit.SECONDS);
            }
        });
    }

    private static void startConsoleThread(Channel channel) {
        Scanner sc = new Scanner(System.in);
        System.out.print("input command: ");
        SINGLE_CONSOLE_POOL.execute(() -> {
            while (true) {
                try {
                    ConsoleCommandManager.execute(channel, sc);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

}
