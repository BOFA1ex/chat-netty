package com.bofa.client.console.commandhandler;

import com.bofa.attribute.UserStatus;
import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import com.bofa.client.service.UserFriendSv;
import com.bofa.client.util.H2TaskManager;
import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.entity.User;
import com.bofa.entity.UserFriend;
import com.bofa.exception.ChatException;
import com.bofa.protocol.request.FriendCRequestPacket;
import com.bofa.session.Session;
import com.bofa.util.LocalDateTimeUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.console.commandhandler
 * @date 2019/5/4
 */
@Component
public class FriendCCommandHandler extends FriendCommandHandler {

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
        if (findFriend(friends, friendMsg) == null) {
            ChatException.throwChatException("找不到 [" + friendMsg + "]");
        }
        PrintStreamDelegate.delegate(() -> {
            System.out.print("删除好友 输入2, 对其隐身 输入3 : ");
        });
        Integer changeStatus = Integer.valueOf(PrintStreamDelegate.nextLine());
        if (changeStatus != UserStatus.VISIBLE.status && changeStatus != UserStatus.OFFLINE.status) {
            ChatException.throwChatException("请按照说明填写正确规范");
        }
        FriendCRequestPacket requestPacket = new FriendCRequestPacket();
        if (friendMsgType(friendMsg)) {
            if (changeStatus == UserStatus.OFFLINE.status){
                H2TaskManager.execute("change friend status by name", () -> {
                    userFriendSv.delete(friendMsg);
                });
            }
            requestPacket.setUserfriendName(friendMsg);
        } else {
            Integer userFriendId = Integer.valueOf(friendMsg);
            if (changeStatus == UserStatus.OFFLINE.status){
                H2TaskManager.execute("change friend status by id", () -> {
                    userFriendSv.delete(userFriendId);
                });
            }
            requestPacket.setUserfriendid(userFriendId);
        }
        requestPacket.setUserid(user.getUserId());
        requestPacket.setOperatetime(LocalDateTimeUtil.now0());
        requestPacket.setStatus(changeStatus);
        System.out.println(requestPacket);
        channel.writeAndFlush(requestPacket);
        return this;
    }

    @Override
    protected ClientCommand getCommand() {
        return ClientCommand.FRIENDC;
    }

    @Override
    protected boolean needBlocked() {
        return false;
    }


}
