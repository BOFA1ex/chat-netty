package com.bofa.client.console.commandhandler;

import com.bofa.attribute.UserStatus;
import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.entity.User;
import com.bofa.exception.ChatException;
import com.bofa.protocol.request.ChangeStatusRequestPacket;
import com.bofa.util.PrintUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.console.commandhandler
 * @date 2019/4/20
 */
@Component
public class StatusCCommandHandler extends BaseConsoleCommand {

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        if (!SessionUtil.hasLogin(channel)) {
            ChatException.throwChatException("切换用户状态失败，当前没有登录的账号");
        }
        ChangeStatusRequestPacket requestPacket = new ChangeStatusRequestPacket();
        User localUser = SessionUtil.getSession(channel).getUser();
        System.out.print("input status: ");
        int status = 0;
        String statusComment;
        try {
            status = Integer.valueOf(PrintStreamDelegate.nextLine());
        } catch (Exception e) {
            ChatException.throwChatException("status 不符合规范 参见UserStatus" + Arrays.toString(UserStatus.values()));
        }
        if (StringUtils.isEmpty((statusComment = UserStatus.findByStatus(status, true)))) {
            ChatException.throwChatException("status 不符合规范 参见UserStatus" + Arrays.toString(UserStatus.values()));
        }
        localUser.setStatus(status);
        PrintUtil.println(localUser.getUserName(), "当前状态: " + statusComment);
        requestPacket.setStatus(status);
        requestPacket.setUserId(localUser.getUserId());
        channel.writeAndFlush(requestPacket);
        return this;
    }

    @Override
    public ClientCommand getCommand() {
        return ClientCommand.STATUSC;
    }

    @Override
    protected boolean needBlocked() {
        return false;
    }
}
