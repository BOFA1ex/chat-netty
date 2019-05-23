package com.bofa.client.console.commandhandler;

import com.bofa.attribute.UserStatus;
import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import com.bofa.entity.User;
import com.bofa.exception.ChatException;
import com.bofa.protocol.request.LogoutRequestPacket;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
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
public class LogoutCommandHandler extends BaseConsoleCommand {

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        if (!SessionUtil.hasLogin(channel)) {
            ChatException.throwChatException("注销失败，当前没有登录的账号");
        }
        User user = SessionUtil.getSession(channel).getUser();
        LogoutRequestPacket requestPacket = new LogoutRequestPacket();
        requestPacket.setUserId(user.getUserId());
        requestPacket.setStatus(UserStatus.OFFLINE.status);
        InetSocketAddress address = (InetSocketAddress) channel.localAddress();
        requestPacket.setCommonIp(address.getHostString());
        channel.writeAndFlush(requestPacket);
        return this;
    }

    @Override
    public ClientCommand getCommand() {
        return ClientCommand.LOGOUT;
    }

    @Override
    protected boolean needBlocked() {
        return true;
    }
}
