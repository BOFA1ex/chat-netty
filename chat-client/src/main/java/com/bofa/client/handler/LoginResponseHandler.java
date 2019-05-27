package com.bofa.client.handler;

import com.bofa.client.service.UserFriendSv;
import com.bofa.client.service.UserNoticeSv;
import com.bofa.client.service.UserSv;
import com.bofa.client.util.H2TaskManager;
import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.entity.User;
import com.bofa.entity.UserFriend;
import com.bofa.entity.UserNotice;
import com.bofa.protocol.response.LoginResponsePacket;
import com.bofa.session.Session;
import com.bofa.util.PrintUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.handler
 * @date 2019/4/2
 */
@ChannelHandler.Sharable
public class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponsePacket> {

    @Autowired
    private UserSv userSv;

    @Autowired
    private UserNoticeSv userNoticeSv;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponsePacket loginResponsePacket) throws Exception {
        User user = loginResponsePacket.getUser();
        List<UserFriend> userFriends = loginResponsePacket.getUserFriends();
        List<UserNotice> userNotices = loginResponsePacket.getUserNotices();
        if (loginResponsePacket.isSuccess()) {
            PrintStreamDelegate.delegate(successAction(user.getUserName(), userNotices));
            SessionUtil.bindSession(new Session(user, userFriends, userNotices), ctx.channel());
            /**
             * save latestUserId when user login but the process has not exit yet.
             * @see UserSv.latestUserId
             */
            userSv.setLatestUserId(user.getUserId());
            H2TaskManager.execute("save user and friends", () -> userSv.save(user, userFriends));
            H2TaskManager.execute("save user notices", () -> userNoticeSv.save(userNotices));
        } else {
            PrintStreamDelegate.delegate(failAction(user.getUserName(), loginResponsePacket.getMessage()));
        }
        SessionUtil.signalRespOrder();
    }

    private static Runnable successAction(String userName, List<UserNotice> userNotices) {
        return () -> {
            PrintUtil.println(userName, "login success");
            for (UserNotice userNotice : userNotices) {
                PrintUtil.println(userNotice);
            }
        };
    }

    private static Runnable failAction(String userName, String message) {
        return () -> PrintUtil.println(userName, "login fail, reason: " + message);
    }

}
