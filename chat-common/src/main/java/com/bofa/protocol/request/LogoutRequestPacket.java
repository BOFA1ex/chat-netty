package com.bofa.protocol.request;

import com.bofa.protocol.command.Command;
import com.bofa.protocol.response.AbstractResponsePacket;
import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol.request
 * @date 2019/4/8
 */
@Data
public class LogoutRequestPacket extends AbstractRequestPacket {

    Integer status;

    String commonIp;

    @Override
    public Byte getCommand() {
        return Command.LOGOUT_REQUEST.command;
    }
}
