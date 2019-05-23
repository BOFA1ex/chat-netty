package com.bofa.client.console.commandhandler;

import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.entity.User;
import com.bofa.entity.UserFriend;
import com.bofa.exception.ChatException;
import com.bofa.protocol.request.FriendARequestPacket;
import com.bofa.session.Session;
import com.bofa.util.LocalDateTimeUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.console.commandhandler
 * @date 2019/4/27
 */
@Component
public class FriendACommandHandler extends FriendCommandHandler {

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        if (!SessionUtil.hasLogin(channel)) {
            ChatException.throwChatException("操作失败，当前没有登录的账号");
        }
        Session session = SessionUtil.getSession(channel);
        User user = session.getUser();
        List<UserFriend> friends = session.getFriends();
        PrintStreamDelegate.delegate(() -> System.out.print("输入操作对象 [用户名 or ID]: "));
        String friendMsg = PrintStreamDelegate.nextLine();
        if (findFriend(friends, friendMsg) != null) {
            ChatException.throwChatException("[" + friendMsg + "] 已是你的好友");
        }
        PrintStreamDelegate.delegate(() -> System.out.print("输入申请信息: "));
        String approvalContent = PrintStreamDelegate.nextLine();
        FriendARequestPacket requestPacket = new FriendARequestPacket();
        if (!friendMsgType(friendMsg)) {
            Integer friendId = Integer.valueOf(friendMsg);
            requestPacket.setApprovaltouserid(friendId);
        }else {
            requestPacket.setApprovaltousername(friendMsg);
        }
        requestPacket.setApprovaluserid(user.getUserId());
        requestPacket.setApprovalusername(user.getUserName());
        requestPacket.setApprovaldatetime(LocalDateTimeUtil.now0());
        requestPacket.setApprovalcontent(approvalContent);
        channel.writeAndFlush(requestPacket);
        return this;
    }



    @Override
    protected ClientCommand getCommand() {
        return ClientCommand.FRIENDA;
    }

    @Override
    protected boolean needBlocked() {
        return true;
    }

    public static void main(String[] args) {
        UserFriend u1 = new UserFriend();
        u1.setUserFriendName("bofa");
        UserFriend u2 = new UserFriend();
        u2.setUserFriendName("yuli");
        List<UserFriend> userFriends = Arrays.asList(u1, u2);
        new FriendACommandHandler().findFriend(userFriends, "yul");
    }
}
