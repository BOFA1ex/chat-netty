package com.bofa.client.console.commandhandler;

import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import com.bofa.client.service.UserMessageSv;
import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.entity.User;
import com.bofa.entity.UserMessage;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.console.commandhandler
 * get chat message which currentUser sent and the chat' user sent.
 * online & offline support.
 * @date 2019/5/19
 */
@Component
public class EchoMessageCommandHandler extends FriendCommandHandler {

    @Autowired
    private UserMessageSv userMessageSv;

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        User user;
        if (SessionUtil.hasLogin(channel)) {
            user = SessionUtil.getSession(channel).getUser();
        }else {
            user = userSv.getUser();
        }
        String userName = user.getUserName();
        PrintStreamDelegate.delegate(() -> System.out.print("输入操作对象 [用户名 or ID]: "));
        String friendMsg = PrintStreamDelegate.nextLine();
        List<UserMessage> userMessages;
        if (friendMsgType(friendMsg)) {
            userMessages = userMessageSv.findByFriendName(friendMsg, userName);
        }else {
            userMessages = userMessageSv.findByFriendId(Integer.valueOf(friendMsg), userName);
        }
        PrintStreamDelegate.delegate(() -> System.out.println(userMessages));
        return this;
    }

    @Override
    protected ClientCommand getCommand() {
        return ClientCommand.MESSAGE;
    }

    @Override
    protected boolean needBlocked() {
        return false;
    }
}
