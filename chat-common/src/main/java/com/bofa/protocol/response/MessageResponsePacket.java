package com.bofa.protocol.response;

import com.bofa.entity.UserMessage;
import com.bofa.protocol.command.Command;
import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol.response
 * @date 2019/4/13
 */
@Data
public class MessageResponsePacket extends AbstractResponsePacket{

    UserMessage userMessage;

    String fromUserName;

    @Override
    public Byte getCommand() {
        return Command.MESSAGE_RESPONSE.command;
    }
}
