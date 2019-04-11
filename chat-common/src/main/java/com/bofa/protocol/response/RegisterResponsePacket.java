package com.bofa.protocol.response;

import com.bofa.entity.User;
import com.bofa.entity.UserFriend;
import com.bofa.protocol.command.Command;
import lombok.Data;

import java.util.List;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol.response
 * @date 2019/4/8
 */
@Data
public class RegisterResponsePacket extends AbstractResponsePacket {

    private User user;

    private List<UserFriend> userFriends;

    @Override
    public Byte getCommand() {
        return Command.REGISTER_RESPONSE.command;
    }
}
