package com.bofa.client.handler;

import com.bofa.attribute.NoticeType;
import com.bofa.client.service.UserFriendSv;
import com.bofa.client.service.UserNoticeSv;
import com.bofa.client.util.H2TaskManager;
import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.entity.UserFriend;
import com.bofa.entity.UserNotice;
import com.bofa.protocol.response.NoticeResponsePacket;
import com.bofa.util.PrintUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.handler
 * @date 2019/4/28
 */
@ChannelHandler.Sharable
public class NoticeResponseHandler extends SimpleChannelInboundHandler<NoticeResponsePacket> {

    @Autowired
    private UserFriendSv userFriendSv;

    @Autowired
    private UserNoticeSv userNoticeSv;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NoticeResponsePacket responsePacket) throws Exception {
        UserNotice notice = responsePacket.getUserNotice();
        UserFriend userFriend = responsePacket.getUserFriend();

        /**
         * 好友申请成功回调，h2 save userFriend
         */
        if (notice.getNoticetype().equals(NoticeType.FRIEND_APPROVAL_MESSAGE_CALLBACK.type)) {
            H2TaskManager.execute("save userFriend", () -> {
                SessionUtil.getSession(ctx.channel()).getFriends().add(userFriend);
                userFriendSv.save(userFriend);
            });
        }
        /**
         * 异地登录，通知本地设备登出
         */
        else if (notice.getNoticetype().equals(NoticeType.OTHER_PLACE_LOGIN.type)) {
            SessionUtil.unbindSession(ctx.channel());
            ctx.close().addListener(future -> {
                Optional.ofNullable(future.cause()).ifPresent(Throwable::printStackTrace);
            });
        }

        PrintStreamDelegate.delegate(() -> System.out.println("\n" + notice));
        H2TaskManager.execute("save notice", () -> userNoticeSv.save(notice));
    }
}
