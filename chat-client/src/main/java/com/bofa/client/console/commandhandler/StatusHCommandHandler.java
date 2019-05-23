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
 * @description com.bofa.client.console.commandhandler
 * @date 2019/4/20
 */
@Component
public class StatusHCommandHandler extends BaseConsoleCommand {

    @Override
    public BaseConsoleCommand commandHandle(Channel channel) {
        System.out.println(Arrays.toString(ClientCommand.STATUS.getOptions()));
        return this;
    }

    @Override
    public ClientCommand getCommand() {
        return ClientCommand.STATUSH;
    }

    @Override
    protected boolean needBlocked() {
        return false;
    }
}
