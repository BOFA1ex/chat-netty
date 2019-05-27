package com.bofa.client.handler;

import com.bofa.attribute.NoticeType;
import com.bofa.client.service.UserMessageSv;
import com.bofa.client.service.UserNoticeSv;
import com.bofa.client.util.H2TaskManager;
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

    @Autowired
    private UserNoticeSv userNoticeSv;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageResponsePacket responsePacket) throws Exception {
        UserMessage userMessage = responsePacket.getUserMessage();
        String fromUserName = userMessage.getFromusername();
        /**
         * denote receive-client get the message successful which [fromUserName] sent.
         */
        Session session = SessionUtil.getSession(ctx.channel());
        UserFriend lastChatUserFriend = session.getLastChatUserFriend();
        /**
         * if lastChatUserFriend is null, set session and assign again.
         * if lastChatUserFriend not equals to [fromUserName], save notice to remind [localUser]
         * else print message info directly and callback [fromUserName] message which sent is read.
         * finally userMessage save in h2-database
         */
        if (lastChatUserFriend == null) {
            session.setLastChatUserFriend(session.findFriendByName(fromUserName));
            lastChatUserFriend = session.getLastChatUserFriend();
        }
        if (!lastChatUserFriend.getUserFriendName().equals(fromUserName)) {
            UserNotice notice = mapper(userMessage);
            PrintStreamDelegate.delegate(() -> PrintUtil.println(fromUserName, " 发来未读信息，请及时查看"));
            H2TaskManager.execute("save notice", () -> userNoticeSv.save(notice));
        } else {
            PrintStreamDelegate.delegate(() -> System.out.println(userMessage));
            ctx.writeAndFlush(new MessageCallBackRequestPacket(
                    userMessage.getFromuserid(), userMessage.getTouserid(), userMessage.getContent()));
        }
        /**
         * there userMessage need reMapper
         */
        H2TaskManager.execute("save message", () -> userMessageSv.save(userMessage));
    }


    /**
     * mapper UserNotice by UserMessage
     * @param userMessage
     * @return
     */
    private UserNotice mapper(UserMessage userMessage) {
        UserNotice notice = new UserNotice();
        notice.setNoticecontent(userMessage.getContent());
        notice.setNoticedatetime(userMessage.getDatetime());
        notice.setNoticeid(userMessage.getFromuserid());
        notice.setNoticename(userMessage.getFromusername());
        notice.setNoticetype(NoticeType.FRIEND_UNREAD_MESSAGE.type);
        notice.setUserid(userMessage.getTouserid());
        notice.setUsername(userMessage.getTousername());
        return notice;
    }
}
