package com.bofa.protocol.response;

import com.bofa.protocol.command.Command;
import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol.response
 * @date 2019/4/8
 */
@Data
public class LogoutResponsePacket extends AbstractResponsePacket {

    String username;

    @Override
    public Byte getCommand() {
        return Command.LOGOUT_RESPONSE.command;
    }
}
