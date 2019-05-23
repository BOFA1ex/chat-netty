package com.bofa.protocol.response;

import com.bofa.entity.UserFriend;
import com.bofa.entity.UserNotice;
import com.bofa.protocol.command.Command;
import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.protocol.response
 * @date 2019/4/28
 */
@Data
public class NoticeResponsePacket extends AbstractResponsePacket{

    public NoticeResponsePacket() {
    }

    public NoticeResponsePacket(UserNotice userNotice) {
        this.userNotice = userNotice;
    }

    public NoticeResponsePacket(UserNotice userNotice, UserFriend userFriend) {
        this.userNotice = userNotice;
        this.userFriend = userFriend;
    }

    UserNotice userNotice;

    UserFriend userFriend;

    @Override
    public Byte getCommand() {
        return Command.NOTICE_RESPONSE.command;
    }
}
