package com.bofa.client.console;

import com.bofa.attribute.UserStatus;
import com.bofa.exception.ChatErrorCode;
import com.bofa.exception.ChatException;
import com.bofa.protocol.request.LoginRequestPacket;
import com.bofa.protocol.request.LogoutRequestPacket;
import com.bofa.protocol.request.RegisterRequestPacket;
import com.bofa.session.Session;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
import org.springframework.util.StringUtils;

import java.io.Console;
import java.util.*;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.console
 * @date 2019/4/6
 */
public class ConsoleCommandManager {

    private static HashMap<ClientCommand, ConsoleCommand> chain = new HashMap<>();

    private static ConsoleCommand HELP = new HelpCommandHandler();

    static {
        chain.put(ClientCommand.LOGIN, new LoginCommandHandler());
        chain.put(ClientCommand.REGISTER, new RegisterCommandHandler());
        chain.put(ClientCommand.HELP, new HelpCommandHandler());
        chain.put(ClientCommand.LOGOUT, new LogoutCommandHandler());
    }

    public static void execute(Channel channel, Scanner scanner) {
        String source = scanner.nextLine();
        if (StringUtils.isEmpty(source)) {
            HELP.commandHandle(channel, scanner);
            return;
        }
        ClientCommand cmd = null;
        try {
            cmd = ClientCommand.valueOf(source.trim().toUpperCase());
        } catch (Exception e) {
            System.out.println("无法识别该指令[" + source.trim().toUpperCase() + "]");
        }
        chain.get(cmd).commandHandle(channel, scanner);
    }
//
//    private static String getMainCommand(String source) {
//        return source.substring(0, source.indexOf(" "));
//    }

    private static String[] split(String source, String delim) {
        StringTokenizer tokenizer = new StringTokenizer(source, " \r\t\n" + delim);
        int count = (tokenizer.countTokens() - 1) / 2;
        String[] result = new String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); i++) {
            result[i] = tokenizer.nextToken();
        }
        String[] response = new String[count];
        for (int i = 2, k = 0; count > 0; i += count, count--) {
            response[k++] = result[i];
        }
        return response;
    }

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
            password = new String(getConsole().readPassword("input your password: "));
            LoginRequestPacket requestPacket = new LoginRequestPacket();
            if (SessionUtil.getSession(channel) != null && SessionUtil.getSession(channel).getUserName().equals(userName)) {
                ChatException.throwChatException(ChatErrorCode.BAD_REQUEST, "客户端已登录[" + SessionUtil.getSession(channel).getUserName() + "]");
            }
            requestPacket.setUsername(userName);
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
            password = new String(getConsole().readPassword("input your password: "));
            RegisterRequestPacket requestPacket = new RegisterRequestPacket();
            requestPacket.setUsername(userName);
            requestPacket.setPassword(password);
            channel.writeAndFlush(requestPacket);
        }
    }

    private static class LogoutCommandHandler implements ConsoleCommand {
        @Override
        public void commandHandle(Channel channel, Scanner scanner) {
            if (!SessionUtil.hasLogin(channel)) {
                ChatException.throwChatException(ChatErrorCode.UNAUTHORIZED, "注销失败，当前没有登录的账号");
            }
            LogoutRequestPacket requestPacket = new LogoutRequestPacket();
            requestPacket.setUserid(SessionUtil.getSession(channel).getUserId());
            requestPacket.setStatus(UserStatus.OFFLINE);
            channel.writeAndFlush(requestPacket);
        }
    }

    private static Console getConsole() {
        Console cons = System.console();
        if (cons == null) {
            System.out.println("Couldn't get Console instance, maybe you're running this from within an IDE?");
        }
        return cons;
    }
}
