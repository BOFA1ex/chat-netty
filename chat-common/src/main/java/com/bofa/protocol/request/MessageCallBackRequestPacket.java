package com.bofa.protocol.request;

import com.bofa.protocol.command.Command;
import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol.request
 * @date 2019/4/13
 */
@Data
public class MessageCallBackRequestPacket extends AbstractRequestPacket{

    public MessageCallBackRequestPacket(Integer fromUserId, Integer toUserId, String content) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.content = content;
    }

    Integer toUserId;

    Integer fromUserId;

    String content;

    @Override
    public Byte getCommand() {
        return Command.NULL.command;
    }
}
