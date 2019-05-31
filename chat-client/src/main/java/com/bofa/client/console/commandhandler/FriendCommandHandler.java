package com.bofa.client.console.commandhandler;

import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import com.bofa.client.console.ConsoleCommandManager;
import com.bofa.client.service.UserFriendSv;
import com.bofa.client.service.UserSv;
import com.bofa.entity.UserFriend;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.console.commandhandler
 * @date 2019/5/4
 */
@Component
public class FriendCommandHandler extends BaseConsoleCommand {

    @Autowired
    protected UserFriendSv userFriendSv;

    @Autowired
    protected UserSv userSv;

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        ConsoleCommandManager.execute(true, ClientCommand.FRIENDL, channel);
        return this;
    }

    @Override
    protected ClientCommand getCommand() {
        return ClientCommand.FRIEND;
    }

    /**
     * 判断输入的是friend id 还是 friend name
     * true : friendName
     * false : friendId
     */
    protected boolean friendMsgType(String friendMsg) {
        for (char c : friendMsg.toCharArray()) {
            if (!Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    protected Integer getFriendId(List<UserFriend> userFriends, String friendName) {
        for (UserFriend userFriend : userFriends) {
            if (userFriend.getUserFriendName().matches(".*?" + friendName + ".*?$")) {
                return userFriend.getUserFriendId();
            }
        }
        return null;
    }

    protected UserFriend findFriend(List<UserFriend> userFriends, String friendMsg) {
        Integer friendId = null;
        String friendName = null;
        if (friendMsgType(friendMsg)) {
            friendName = friendMsg;
        } else {
            friendId = Integer.valueOf(friendMsg);
        }
        for (UserFriend friend : userFriends) {
            if (StringUtils.isNotEmpty(friendName)) {
                if (friend.getUserFriendName().matches(".*?" + friendName + ".*?$")) {
                    return friend;
                }
            }
            if (friendId != null) {
                if (friend.getUserFriendId().equals(friendId)) {
                    return friend;
                }
            }
        }
        return null;
    }

    @Override
    protected boolean needBlocked() {
        return false;
    }
}
