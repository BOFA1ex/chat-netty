package com.bofa.client.console.commandhandler;

import com.bofa.attribute.UserStatus;
import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import com.bofa.client.service.UserSv;
import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.entity.UserFriend;
import com.bofa.util.PrintUtil;
import com.bofa.util.SessionUtil;
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
public class FriendLACommandHandler extends FriendCommandHandler {

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        List<UserFriend> userFriends;
        boolean isOnline = false;
        if (!SessionUtil.hasLogin(channel)) {
            userFriends = userSv.getUserFriends();
        } else {
            userFriends = SessionUtil.getSession(channel).getFriends();
            isOnline = true;
        }
        final boolean flag = isOnline;
        PrintStreamDelegate.delegate(() -> {
            System.out.println("[ ---- 好友列表 ---- ]");
            StringBuilder all = new StringBuilder();
            for (int i = 0; i < userFriends.size(); i++) {
                UserFriend userFriend = userFriends.get(i);
                StringBuilder sb = new StringBuilder();
                sb.append(">>").append(" ").append(userFriend.getUserFriendName());
                String remark = userFriend.getRemark();
                if (StringUtils.isNotEmpty(remark)) {
                    sb.append("\t[").append(remark).append("]").append("\t");
                }
                /**
                 * 如果已登录，则返回所有的好友信息
                 */
                if (flag) {
                    sb.append("\t[").append(UserStatus.findByStatus(userFriend.getStatus(), false))
                            .append("]");
                }
                sb.append("\n");
                all.append(sb);
            }
            if (all.length() == 0) {
                System.out.println(">> 暂无好友");
            } else {
                System.out.print(all.toString());
            }
        });
        return this;
    }

    @Override
    protected ClientCommand getCommand() {
        return ClientCommand.FRIENDLA;
    }

    @Override
    protected boolean needBlocked() {
        return false;
    }
}
