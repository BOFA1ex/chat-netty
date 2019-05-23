package com.bofa.client.console.commandhandler;

import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.exception.ChatException;
import com.bofa.protocol.request.LoginRequestPacket;
import com.bofa.session.Session;
import com.bofa.util.ConsoleBuilder;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.console.commandhandler
 * @date 2019/4/20
 */
@Component
public class LoginCommandHandler extends BaseConsoleCommand {

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        Session session = SessionUtil.getSession(channel);
        String[] source = inputUserNameAndPwd();
        LoginRequestPacket requestPacket = new LoginRequestPacket();
        if (session != null) {
            ChatException.throwChatException("客户端已登录["
                    + session.getUser().getUserName() + "]");
        }
        requestPacket.setUserName(source[0]);
        requestPacket.setPassword(source[1]);
        InetSocketAddress address = (InetSocketAddress) channel.localAddress();
        requestPacket.setCommonIp(address.getHostString());
        channel.writeAndFlush(requestPacket).addListener(future -> {
            if (!future.isSuccess()) {
                ChatException.throwChatException("连接超时，请检查网络情况", future.cause().fillInStackTrace());
            }
        });
        return this;
    }

    @Override
    public ClientCommand getCommand() {
        return ClientCommand.LOGIN;
    }

    @Override
    protected boolean needBlocked() {
        return true;
    }

    protected static String[] inputUserNameAndPwd() {
        String userName, password;
        PrintStreamDelegate.delegate(() -> System.out.print("input your username: "));

        userName = PrintStreamDelegate.nextLine();
        if (StringUtils.isEmpty(userName)) {
            ChatException.throwChatException("userName不可为空");
        }
        password = PrintStreamDelegate.nextLine();
//        password = new String(ConsoleBuilder.getConsole().readPassword("input your password: "));
        if (StringUtils.isEmpty(password)) {
            ChatException.throwChatException("password不可为空");
        }
        return new String[]{userName, password};
    }
}
