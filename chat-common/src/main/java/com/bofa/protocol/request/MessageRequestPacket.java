package com.bofa.protocol.request;

import com.bofa.entity.MessageInfo;
import com.bofa.protocol.command.Command;
import lombok.Data;

import java.util.List;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol.request
 * @date 2019/4/11
 */
@Data
public class MessageRequestPacket extends AbstractRequestPacket {

    List<Integer> userIds;

    MessageInfo messageInfo;

    @Override
    public Byte getCommand() {
        return Command.MESSAGE_REQUEST.command;
    }
}