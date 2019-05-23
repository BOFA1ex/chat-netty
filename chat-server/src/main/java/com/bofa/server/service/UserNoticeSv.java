package com.bofa.server.service;

import com.bofa.entity.UserNotice;
import com.bofa.protocol.command.Command;
import com.bofa.protocol.response.AbstractResponsePacket;
import com.bofa.protocol.response.NoticeResponsePacket;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.server.service
 * @date 2019/4/16
 */
public class UserNoticeSv extends BaseSv {

    public static NoticeResponsePacket saveNotice(UserNotice request) {
        return post(Command.SAVE_NOTICE.url, request, NoticeResponsePacket.class);
    }

}
