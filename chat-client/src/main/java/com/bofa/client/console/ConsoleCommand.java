package com.bofa.client.console;

import com.bofa.client.util.PrintUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;

import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.UnaryOperator;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.console
 * @date 2019/4/6
 */
public interface ConsoleCommand {

    /**
     * Execute the policy based on console input instructions
     *
     * @param channel
     * @param scanner
     * @return ConsoleCommand
     */
    ConsoleCommand commandHandle(Channel channel, Scanner scanner);

    int DEFAULT_TIMEOUT = 10;

    /**
     * use CyclicBarrier to waiting for resp
     *
     * @param cmd
     * @param timeout
     */
    default void waitingForResp(ClientCommand cmd, int timeout) {
        try {
            SessionUtil.waitingForResp(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | BrokenBarrierException e) {
            if (e instanceof TimeoutException) {
                PrintUtil.println(cmd.name(), "连接超时, 请检查本地网络");
            } else if (e instanceof InterruptedException) {
                PrintUtil.println(cmd.name(), "连接中断");
            }
            SessionUtil.resetRespOrder();
        }
    }
}
