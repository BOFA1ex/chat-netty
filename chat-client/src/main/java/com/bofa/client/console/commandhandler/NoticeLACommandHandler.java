package com.bofa.client.console.commandhandler;

import com.bofa.attribute.NoticeStatus;
import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import com.bofa.client.service.UserNoticeSv;
import com.bofa.client.service.UserSv;
import com.bofa.entity.User;
import com.bofa.entity.UserNotice;
import com.bofa.session.Session;
import com.bofa.util.PrintUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.console.commandhandler
 * @date 2019/4/24
 */
@Component
public class NoticeLACommandHandler extends NoticeCommandHandler {

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        User user = userSv.getUser();
        /**
         * get all-userNotices from h2-database
         */
        List<UserNotice> userNotices = userSv.getUserNotices();
        return handleUserNotices(channel, userNotices, user);
    }

    @Override
    protected ClientCommand getCommand() {
        return ClientCommand.NOTICELA;
    }

    @Override
    protected boolean needBlocked() {
        return false;
    }
}
