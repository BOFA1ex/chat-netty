package com.bofa.client.console.commandhandler;

import com.bofa.attribute.UserStatus;
import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import com.bofa.entity.User;
import com.bofa.exception.ChatException;
import com.bofa.util.PrintUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.console.commandhandler
 * @date 2019/4/20
 */
@Component
public class StatusLCommandHandler extends BaseConsoleCommand {

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        if (!SessionUtil.hasLogin(channel)) {
            ChatException.throwChatException("查看用户状态失败，当前没有登录的账号");
        }
        User user = SessionUtil.getSession(channel).getUser();
        String status = UserStatus.findByStatus(user.getStatus(), true);
        PrintUtil.println(user.getUserName(), "当前状态: " + status);
        return this;
    }

    @Override
    public ClientCommand getCommand() {
        return ClientCommand.STATUSL;
    }

    @Override
    protected boolean needBlocked() {
        return false;
    }
}
