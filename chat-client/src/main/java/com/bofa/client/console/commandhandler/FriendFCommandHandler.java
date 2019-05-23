package com.bofa.client.console.commandhandler;

import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.entity.User;
import com.bofa.entity.UserFriend;
import com.bofa.exception.ChatException;
import com.bofa.session.Session;
import com.bofa.util.PrintUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author Bofa
 * @version 1.0
 * @description find friend and show its info.
 * online, offline support.
 * @date 2019/5/19
 */
@Component
public class FriendFCommandHandler extends FriendCommandHandler {

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        List<UserFriend> userFriends;
        if (SessionUtil.hasLogin(channel)) {
            userFriends = SessionUtil.getSession(channel).getFriends();
        } else {
            userFriends = userSv.getUserFriends();
        }
        PrintStreamDelegate.delegate(() -> System.out.print("输入查询对象 [用户名 or ID]: "));
        String friendMsg = PrintStreamDelegate.nextLine();
        UserFriend userFriend;
        if (!friendMsgType(friendMsg)) {
            Integer friendId = Integer.valueOf(friendMsg);
            userFriend = userFriendSv.get(friendId);
        } else {
            userFriend = findFriend(userFriends, friendMsg);
        }
        if (userFriend == null){
            PrintStreamDelegate.delegate(() -> {
                System.out.println(">> 查无此人");
            });
        }else {
            PrintStreamDelegate.delegate(() -> {
                System.out.println(userFriend);
            });
        }
        return this;
    }

    @Override
    protected ClientCommand getCommand() {
        return ClientCommand.FRIENDF;
    }

    @Override
    protected boolean needBlocked() {
        return false;
    }
}
