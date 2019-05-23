package com.bofa.client.console.commandhandler;

import com.bofa.client.console.BaseConsoleCommand;
import com.bofa.client.console.ClientCommand;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.console.commandHandler
 * @date 2019/4/20
 */
@Component
public class HelpCommandHandler extends BaseConsoleCommand {

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        System.out.println(ClientCommand.returnAllCommand());
        return this;
    }

    @Override
    public ClientCommand getCommand() {
        return ClientCommand.HELP;
    }

    @Override
    protected boolean needBlocked() {
        return false;
    }
}
