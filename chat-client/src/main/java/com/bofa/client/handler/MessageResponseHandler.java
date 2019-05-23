package com.bofa.client.handler;

import com.bofa.attribute.NoticeType;
import com.bofa.client.service.UserMessageSv;
import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.entity.UserFriend;
import com.bofa.entity.UserMessage;
import com.bofa.entity.UserNotice;
import com.bofa.protocol.request.MessageCallBackRequestPacket;
import com.bofa.protocol.response.MessageResponsePacket;
import com.bofa.session.Session;
import com.bofa.util.PrintUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.handler
 * @date 2019/4/13
 */

@ChannelHandler.Sharable
public class MessageResponseHandler extends SimpleChannelInboundHandler<MessageResponsePacket> {

    @Autowired
    private UserMessageSv userMessageSv;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageResponsePacket responsePacket) throws Exception {
        UserMessage userMessage = responsePacket.getUserMessage();
        String fromUserName = userMessage.getFromusername();
        /**
         * denote receive-client get the message successful which send-client sent.
         */
        Session session = SessionUtil.getSession(ctx.channel());
        UserFriend lastChatUserFriend = session.getLastChatUserFriend();
        /**
         * 如果接收方当前聊天对象为空, set it and assign again.
         * 如果接收方当前聊天对象不为[fromUserName]
         * save Notice 由接收方手动查看信息通知
         * 反之, 打印聊天信息, 并回调通知信息已读
         * finally userMessage 需要存入h2
         */
        if (lastChatUserFriend == null) {
            session.setLastChatUserFriend(session.findFriendByName(fromUserName));
            lastChatUserFriend = session.getLastChatUserFriend();
        }
        if (lastChatUserFriend.getUserFriendName().equals(fromUserName)) {
            UserNotice notice = new UserNotice();
            notice.setNoticecontent(userMessage.getContent());
            notice.setNoticedatetime(userMessage.getDatetime());
            notice.setNoticeid(userMessage.getFromuserid());
            notice.setNoticename(userMessage.getFromusername());
            notice.setNoticetype(NoticeType.FRIEND_UNREAD_MESSAGE.type);
            notice.setUserid(userMessage.getTouserid());
            notice.setUsername(userMessage.getTousername());
//            H2TaskManager.execute("save notice", () -> userNoticeSv.saveUnReadMessage(notice));
        } else {
            PrintStreamDelegate.delegate(() -> {
                PrintUtil.println(fromUserName, " -> " + userMessage.getContent());
            });
            ctx.writeAndFlush(new MessageCallBackRequestPacket(
                    userMessage.getFromuserid(), userMessage.getTouserid(), userMessage.getContent()));
        }
//        H2TaskManager.execute("save message", () -> userMessageSv.save(userMessage));
    }

}
