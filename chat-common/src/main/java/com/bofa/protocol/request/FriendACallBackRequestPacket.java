package com.bofa.protocol.request;

import com.bofa.entity.UserNotice;
import com.bofa.protocol.command.Command;
import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.protocol.request
 * @date 2019/4/28
 */
@Data
public class FriendACallBackRequestPacket extends AbstractRequestPacket {

    public FriendACallBackRequestPacket() {

    }

    public FriendACallBackRequestPacket(UserNotice userNotice) {
        this.userNotice = userNotice;
    }

    UserNotice userNotice;

    @Override
    public Byte getCommand() {
        return Command.FRIENDA_CALLBACK_REQUEST.command;
    }
}
