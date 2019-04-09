package com.bofa.protocol.request;

import com.bofa.protocol.command.Command;
import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol.request
 * @date 2019/4/8
 */
@Data
public class RegisterRequestPacket extends AbstractRequestPacket {

    String username;

    String password;

    @Override
    public Byte getCommand() {
        return Command.REGISTER_REQUEST.command;
    }
}
