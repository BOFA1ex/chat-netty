package com.bofa.protocol.response;

import com.bofa.protocol.Packet;
import com.bofa.protocol.command.Command;
import lombok.Data;

import java.util.List;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol.response
 * @date 2019/4/2
 */
@Data
public class LoginResponsePacket extends AbstractResponsePacket {

    Integer userid;

    String username;

    Integer status;

    @Override
    public Byte getCommand() {
        return Command.LOGIN_RESPONSE.command;
    }
}
