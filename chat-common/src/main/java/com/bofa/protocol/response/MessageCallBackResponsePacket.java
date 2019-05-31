package com.bofa.protocol.response;

import com.bofa.protocol.command.Command;
import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol.response
 * @date 2019/4/13
 */
@Data
public class MessageCallBackResponsePacket extends AbstractResponsePacket {

    public MessageCallBackResponsePacket() {
    }

    public MessageCallBackResponsePacket(String content, String toUserName, boolean success) {
        this.content = content;
        this.toUserName = toUserName;
        super.success = success;
    }

    String content;

    String toUserName;

    @Override
    public Byte getCommand() {
        return Command.MESSAGE_CALLBACK_RESPONSE.command;
    }
}
