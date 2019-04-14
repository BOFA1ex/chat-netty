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
public class ChangeStatusResponsePacket extends AbstractResponsePacket{

    Integer status;

    @Override
    public Byte getCommand() {
        return Command.CHANGE_STATUS_RESPONSE.command;
    }
}
