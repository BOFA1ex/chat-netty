package com.bofa.protocol.request;

import com.bofa.protocol.Packet;
import com.bofa.protocol.command.Command;
import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol.request
 * @date 2019/4/2
 */
@Data
public class LoginRequestPacket extends AbstractRequestPacket {

    String userName;

    String password;

    @Override
    public Byte getCommand() {
        return Command.LOGIN_REQUEST.command;
    }

}
