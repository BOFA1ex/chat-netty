package com.bofa.client.console.commandhandler;

import com.bofa.attribute.NoticeStatus;
import com.bofa.attribute.NoticeType;
import com.bofa.attribute.UserStatus;
import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import com.bofa.client.service.UserSv;
import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.entity.User;
import com.bofa.entity.UserNotice;
import com.bofa.session.Session;
import com.bofa.util.PrintUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.console.commandHandler
 * @date 2019/4/20
 */
@Component
public class NoticeLCommandHandler extends NoticeCommandHandler {

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        User user = userSv.getUser();
        PrintStreamDelegate.delegate(() -> System.out.println("输入通知类型(默认为全部类型) " + Arrays.toString(NoticeType.values())));
        String noticeType = PrintStreamDelegate.nextLine();
        Integer type = null;
        if (StringUtils.isNotEmpty(noticeType)) {
            type = Integer.valueOf(noticeType);
        }
        List<UserNotice> userNotices = userSv.getUserNonReadNoticesByType(type);
        return handleUserNotices(channel, userNotices, user);
    }

    @Override
    public ClientCommand getCommand() {
        return ClientCommand.NOTICEL;
    }

    @Override
    protected boolean needBlocked() {
        return false;
    }
}
