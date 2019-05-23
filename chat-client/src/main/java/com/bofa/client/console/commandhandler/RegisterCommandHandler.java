package com.bofa.client.console.commandhandler;

import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import com.bofa.client.service.UserSv;
import com.bofa.protocol.request.RegisterRequestPacket;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

import static com.bofa.client.console.commandhandler.LoginCommandHandler.inputUserNameAndPwd;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.console.commandhandler
 * @date 2019/4/20
 */
@Component
public class RegisterCommandHandler extends BaseConsoleCommand {

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        String[] source = inputUserNameAndPwd();

        RegisterRequestPacket requestPacket = new RegisterRequestPacket();
        requestPacket.setUserName(source[0]);
        requestPacket.setPassword(source[1]);
        channel.writeAndFlush(requestPacket);
        return this;
    }

    @Override
    public ClientCommand getCommand() {
        return ClientCommand.REGISTER;
    }

    @Override
    protected boolean needBlocked() {
        return true;
    }
}
