package com.bofa.client.console.commandhandler;

import com.bofa.attribute.NoticeStatus;
import com.bofa.attribute.NoticeType;
import com.bofa.client.config.ApplicationContextCmpt;
import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import com.bofa.client.console.ConsoleCommandManager;
import com.bofa.client.handler.FriendACallBackRequestHandler;
import com.bofa.client.service.UserNoticeSv;
import com.bofa.client.service.UserSv;
import com.bofa.client.util.H2TaskManager;
import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.entity.User;
import com.bofa.entity.UserNotice;
import com.bofa.protocol.response.AbstractResponsePacket;
import com.bofa.protocol.response.FriendACallBackResponsePacket;
import com.bofa.session.Session;
import com.bofa.util.PrintUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.bofa.protocol.response.AbstractResponsePacket.CODE_SC;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.console.commandhandler
 * @date 2019/4/24
 */
@Component
public class NoticeCommandHandler extends BaseConsoleCommand {

    @Autowired
    protected UserSv userSv;

    @Autowired
    private UserNoticeSv userNoticeSv;

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        ConsoleCommandManager.execute(ClientCommand.NOTICEL, channel);
        return this;
    }

    /**
     * change notice status
     *
     * @return
     */
    public void changeNoticeStatus(List<UserNotice> noticeInfos) {
        H2TaskManager.execute("change notice status", () -> {
            userNoticeSv.update(noticeInfos);
        });
    }

    protected BaseConsoleCommand handleUserNotices(Channel channel, List<UserNotice> userNotices, User user) {
        String userName = user.getUserName();

        /**
         * online-status
         * if get new-unread userNotices, all-userNotices addAll it.
         */
        if (userNotices.size() == 0) {
            PrintUtil.println(userName, "不存在未读通知");
            return this;
        }

        handleNotice0(userNotices, channel);
        /**
         * change notice status unread -> read
         */
        changeNoticeStatus(userNotices);
        return this;
    }

    private void handleNotice0(List<UserNotice> noticeInfos, Channel channel) {
        for (UserNotice userNotice : noticeInfos) {
            /**
             * notice -la : just print it.
             * notice -l : process depends on type which is friend approval or the other type
             */
            if (Objects.equals(userNotice.getNoticestatus(), NoticeStatus.READ.status)
                    || !Objects.equals(userNotice.getNoticetype(), NoticeType.FRIEND_APPROVAL_MESSAGE.type)) {
                System.out.println(userNotice);
            } else {
                FriendACallBackRequestHandler handler = (FriendACallBackRequestHandler)
                        ApplicationContextCmpt.getBean("friendACallBackRequestHandler");
                FriendACallBackResponsePacket response = handler.handleApprovalCallBack(userNotice);
                channel.writeAndFlush(response).addListener(future -> {
                    Optional.ofNullable(future.cause()).ifPresent(Throwable::printStackTrace);
                    if (future.isSuccess()) {
                        System.out.println("对方已成为你的好友，可以聊天了");
                    }
                });
            }
            userNotice.setNoticestatus(NoticeStatus.READ.status);
        }
    }


    @Override
    protected ClientCommand getCommand() {
        return ClientCommand.NOTICE;
    }

    @Override
    protected boolean needBlocked() {
        return false;
    }
}
