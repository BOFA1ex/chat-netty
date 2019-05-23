package com.bofa.client.console;

import com.bofa.exception.ChatException;
import com.bofa.util.PrintUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
import org.springframework.beans.factory.InitializingBean;

import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.UnaryOperator;

/**
 * @author Bofa
 * @version 1.0
 * 实现InitializingBean接口
 * 注入beanFactory后实现afterPropertiesSet方法
 * 与init-method原理一致，afterPropertiesSet在init-method之前执行
 * 注入到ConsoleCommandManager的缓存
 * @date 2019/4/6
 * @see ConsoleCommandManager
 */
public abstract class BaseConsoleCommand implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        ConsoleCommandManager.putCommandHandler(getCommand(), this);
    }

    /**
     * Execute the policy based on console input instructions
     *
     * @param channel
     * @return ConsoleCommand
     */
    public abstract BaseConsoleCommand commandHandle(Channel channel);

    /**
     * according to the command to make sure which handler.
     *
     * @return
     */
    protected abstract ClientCommand getCommand();

    /**
     * does command is asynchronous or need  blocked for waiting.
     *
     * @return
     */
    protected abstract boolean needBlocked();

    /**
     * use CyclicBarrier to waiting for resp
     *
     * @param cmd
     */
    void waitingForResp(ClientCommand cmd) {
        if (needBlocked()) {
            try {
                SessionUtil.waitingForResp();
            } catch (InterruptedException | BrokenBarrierException e) {
                if (e instanceof InterruptedException) {
                    PrintUtil.println(cmd.name(), "连接中断");
                }
                SessionUtil.resetRespOrder();
            }
        }
    }

}
