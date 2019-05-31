package com.bofa.client.console;

import com.bofa.client.util.PrintStreamDelegate;
import com.bofa.exception.ChatException;
import com.bofa.util.*;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.console
 * @date 2019/4/6
 */
public class ConsoleCommandManager {

    /**
     * 由BeanFactoryPostProcessor处理commandHandler
     * 缓存到ConsoleCommandManager的commandHashMap中
     *
     * @see com.bofa.client.config.MapperConfig
     */
    private static HashMap<ClientCommand, BaseConsoleCommand> commandHashMap = new HashMap<>();


    static void putCommandHandler(ClientCommand key, BaseConsoleCommand value) {
        commandHashMap.put(key, value);
    }

    /**
     * 应用启动首次加载Help说明
     */
    private static boolean isFirst = true;

    /**
     * 由指令判定缓存中的commandHandler来执行commandHandle
     *
     * @param channel
     * @see com.bofa.client.console.commandhandler
     */
    public static void execute(Channel channel) throws ChatException {
        if (isFirst) {
            commandHashMap.get(ClientCommand.HELP).commandHandle(channel);
            isFirst = false;
        }
        ClientCommand cmd = null;
        boolean commandInvalid = true;
        String command = PrintStreamDelegate.nextLine();
        try {
            command = StringTokenUtil.merge(command, "-");
            cmd = ClientCommand.valueOf(command);
        } catch (Exception e) {
            final String finalCommand = command;
            PrintStreamDelegate.delegate(() -> {
                System.out.println("无法识别该指令[" + finalCommand + "]");
            });
            commandInvalid = false;
        }
        execute(commandInvalid, cmd, channel);
    }


    /**
     * @param cmd
     * @param channel
     * @see com.bofa.client.console.commandhandler.NoticeCommandHandler
     */
    public static void execute(boolean commandInvalid, ClientCommand cmd, Channel channel) {
        if (commandInvalid) {
            commandHashMap.get(cmd).commandHandle(channel).waitingForResp(cmd);
        }
    }

}
