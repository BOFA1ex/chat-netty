package com.bofa.protocol.request;

import com.bofa.protocol.command.Command;
import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol.request
 * @date 2019/4/11
 */
@Data
public class MessageRequestPacket extends AbstractRequestPacket {

    Integer userId;

    Integer userFriendId;

    String message;

    String dateTime;

    Integer messageType;

    Integer status;

    @Override
    public Byte getCommand() {
        return Command.MESSAGE_REQUEST.command;
    }
}
