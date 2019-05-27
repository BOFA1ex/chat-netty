package com.bofa.client.console.commandhandler;

import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import com.bofa.client.service.UserSv;
import com.bofa.exception.ChatException;
import com.bofa.protocol.request.RegisterRequestPacket;
import com.bofa.session.Session;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Scanner;

import static com.bofa.client.console.commandhandler.LoginCommandHandler.inputUserNameAndPwd;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.console.commandhandler
 * @date 2019/4/20
 */
@Component
public class RegisterCommandHandler extends BaseConsoleCommand {

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        Session session = SessionUtil.getSession(channel);
        if (session != null) {
            ChatException.throwChatException("客户端已登录["
                    + session.getUser().getUserName() + "]");
        }
        String[] source = inputUserNameAndPwd();

        RegisterRequestPacket requestPacket = new RegisterRequestPacket();
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
        return ClientCommand.REGISTER;
    }

    @Override
    protected boolean needBlocked() {
        return true;
    }
}
