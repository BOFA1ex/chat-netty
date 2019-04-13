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
public class ChangeStatusRequestPacket extends AbstractRequestPacket{

    Integer status;

    @Override
    public Byte getCommand() {
        return Command.CHANGE_STATUS_REQUEST.command;
    }
}
