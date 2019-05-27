package com.bofa.client.console.commandhandler;

import com.bofa.attribute.MessageType;
import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import com.bofa.client.service.UserMessageSv;
import com.bofa.client.util.H2TaskManager;
import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.entity.UserFriend;
import com.bofa.entity.UserMessage;
import com.bofa.exception.ChatException;
import com.bofa.protocol.request.MessageRequestPacket;
import com.bofa.session.Session;
import com.bofa.util.LocalDateTimeUtil;
import com.bofa.util.SessionUtil;
import com.bofa.util.StringTokenUtil;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.console.commandhandler
 * @date 2019/4/20
 */
@Component
public class SendMessageCommandHandler extends BaseConsoleCommand {

    @Autowired
    private UserMessageSv userMessageSv;

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        if (!SessionUtil.hasLogin(channel)) {
            ChatException.throwChatException("发送失败，当前没有登录的账号");
        }
        Session session = SessionUtil.getSession(channel);

        System.out.print("input your friendNames[one or more]: ");
        String[] friendNames = StringTokenUtil.split(PrintStreamDelegate.nextLine(), " ,\r\t\n");
        System.out.print("input your message: ");
        String content = PrintStreamDelegate.nextLine();
        int userId = session.getUser().getUserId();
        String userName = session.getUser().getUserName();

        for (String toUserName : friendNames) {
            int userFriendId;
            UserFriend userFriend = session.findFriendByName(toUserName);
            /**
             * 好友列表缓存判断是否存在该好友对象
             */
            if (userFriend == null) {
                ChatException.throwChatException("找不到该好友[" + toUserName + "]");
            }
            userFriendId = userFriend.getUserFriendId();
            /**
             * 设置当前单聊对象
             * 如果对方在线并回复信息即直接接收信息，反之其他对象只收到信息通知, 需要用户查看信息通知
             */
            session.setLastChatUserFriend(userFriend);
            /**
             * mapper userMessage
             * @see SendMessageCommandHandler#mapper(String, Integer, Integer, String, String)
             */
            UserMessage messageInfo = mapper(content, userId, userFriendId, userName, toUserName);
            MessageRequestPacket requestPacket = new MessageRequestPacket();
            requestPacket.setUserIds(new ArrayList<>(Arrays.asList(userId, userFriendId)));
            requestPacket.setUserMessage(messageInfo);
            /**
             * 本地客户端保存聊天内容
             */
            H2TaskManager.execute("save message", () -> userMessageSv.save(messageInfo));
            channel.writeAndFlush(requestPacket);
        }
        return this;
    }

    private UserMessage mapper(String content, Integer userId, Integer userFriendId, String userName, String toUserName) {
        UserMessage messageInfo = new UserMessage();
        messageInfo.setContent(content);
        messageInfo.setDatetime(LocalDateTimeUtil.now0());
        messageInfo.setFromuserid(userId);
        messageInfo.setFromusername(userName);
        messageInfo.setTouserid(userFriendId);
        messageInfo.setTousername(toUserName);
        messageInfo.setMessagetype(MessageType.TEXT.type);
        messageInfo.setMessageid(UUID.randomUUID().toString());
        return messageInfo;
    }

    @Override
    public ClientCommand getCommand() {
        return ClientCommand.SEND;
    }

    @Override
    protected boolean needBlocked() {
        return false;
    }

}
