package com.bofa.client.console;

import com.bofa.attribute.UserStatus;
import com.bofa.client.util.ConsoleBuilder;
import com.bofa.client.util.StringTokenUtil;
import com.bofa.entity.UserFriend;
import com.bofa.exception.ChatErrorCode;
import com.bofa.exception.ChatException;
import com.bofa.protocol.request.LoginRequestPacket;
import com.bofa.protocol.request.LogoutRequestPacket;
import com.bofa.protocol.request.RegisterRequestPacket;
import com.bofa.session.Session;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Console;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.console
 * @date 2019/4/6
 */
public class ConsoleCommandManager {

    private static HashMap<ClientCommand, ConsoleCommand> commandHashMap = new HashMap<>();

    private static final ConsoleCommand HELP = new HelpCommandHandler();

    static {
        commandHashMap.put(ClientCommand.LOGIN, new LoginCommandHandler());
        commandHashMap.put(ClientCommand.REGISTER, new RegisterCommandHandler());
        commandHashMap.put(ClientCommand.HELP, new HelpCommandHandler());
        commandHashMap.put(ClientCommand.LOGOUT, new LogoutCommandHandler());
    }

    public static void execute(Channel channel, Scanner scanner) {
        System.out.print("input command: ");
        String source = scanner.nextLine();
        if (StringUtils.isEmpty(source)) {
            HELP.commandHandle(channel, scanner);
            return;
        }
        ClientCommand cmd = null;
        source = source.trim().toUpperCase();
        try {
            cmd = ClientCommand.valueOf(source);
        } catch (Exception e) {
            System.out.println("无法识别该指令[" + source + "]");
        }
        commandHashMap.get(cmd).commandHandle(channel, scanner);
    }
//
//    private static String getMainCommand(String source) {
//        return source.substring(0, source.indexOf(" "));
//    }

    static class HelpCommandHandler implements ConsoleCommand {
        @Override
        public void commandHandle(Channel channel, Scanner scanner) {
            for (ClientCommand c : ClientCommand.values()) {
                System.out.println(Arrays.toString(c.options));
            }
        }
    }

    static class LoginCommandHandler implements ConsoleCommand {

        @Override
        public void commandHandle(Channel channel, Scanner scanner) {
            String userName, password;
            System.err.print("input your username: ");
            userName = scanner.nextLine();
            password = new String(ConsoleBuilder.INSTANCE.readPassword("input your password: "));
            LoginRequestPacket requestPacket = new LoginRequestPacket();
            if (SessionUtil.getSession(channel) != null
                    && SessionUtil.getSession(channel).getUser().getUserName().equals(userName)) {
                ChatException.throwChatException(ChatErrorCode.BAD_REQUEST, "客户端已登录["
                        + SessionUtil.getSession(channel).getUser().getUserName() + "]");
            }
            requestPacket.setUserName(userName);
            requestPacket.setPassword(password);
            channel.writeAndFlush(requestPacket);
        }
    }

    static class RegisterCommandHandler implements ConsoleCommand {

        @Override
        public void commandHandle(Channel channel, Scanner scanner) {
            String userName, password;
            System.err.print("input your username: ");
            userName = scanner.nextLine();
            password = new String(ConsoleBuilder.INSTANCE.readPassword("input your password: "));
            RegisterRequestPacket requestPacket = new RegisterRequestPacket();
            requestPacket.setUserName(userName);
            requestPacket.setPassword(password);
            channel.writeAndFlush(requestPacket);
        }
    }

    static class LogoutCommandHandler implements ConsoleCommand {
        @Override
        public void commandHandle(Channel channel, Scanner scanner) {
            if (!SessionUtil.hasLogin(channel)) {
                System.out.println("注销失败，当前没有登录的账号");
            }
            LogoutRequestPacket requestPacket = new LogoutRequestPacket();
            requestPacket.setUserId(SessionUtil.getSession(channel).getUser().getUserId());
            requestPacket.setStatus(UserStatus.OFFLINE);
            channel.writeAndFlush(requestPacket);
        }
    }

    static class MessageCommandHandle implements ConsoleCommand {
        @Override
        public void commandHandle(Channel channel, Scanner scanner) {
            if (!SessionUtil.hasLogin(channel)) {
                ChatException.throwChatException(ChatErrorCode.UNAUTHORIZED, "注销失败，当前没有登录的账号");
            }
            String[] friendNames = StringTokenUtil.split(scanner.nextLine(), " ,\r\t\n");
            Stream.of(friendNames).forEach(friendName -> {
                SessionUtil.getSession(channel).getFriends().forEach(
                        userFriend -> {
                            if (userFriend.getUserName().equals(friendName)) {
                            }
                        }
                );
            });
            for (String name : friendNames) {
                int userFriendId;
                boolean flag = false;
                for (UserFriend uf : SessionUtil.getSession(channel).getFriends()) {
                    if (uf.getUserName().equals(name)) {
                        userFriendId = uf.getUserId();
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    ChatException.throwChatException(ChatErrorCode.Parameter_invalid, "找不到该好友[" + name + "]");
                }
            }

        }
    }
}
