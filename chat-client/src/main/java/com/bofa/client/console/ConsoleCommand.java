package com.bofa.client.console;

import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.console
 * @date 2019/4/6
 */
public interface ConsoleCommand {

    /**
     * Execute the policy based on console input instructions
     * @param channel
     * @param scanner
     */
    void commandHandle(Channel channel, Scanner scanner);
}
