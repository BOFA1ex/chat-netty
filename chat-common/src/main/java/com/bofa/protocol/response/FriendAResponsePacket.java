package com.bofa.protocol.response;

import com.bofa.protocol.command.Command;
import com.bofa.protocol.request.AbstractRequestPacket;
import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.protocol.response
 * @date 2019/4/27
 */
@Data
public class FriendAResponsePacket extends AbstractResponsePacket{

    String toUserName;

    @Override
    public Byte getCommand() {
        return Command.FRIENDA_RESPONSE.command;
    }
}
