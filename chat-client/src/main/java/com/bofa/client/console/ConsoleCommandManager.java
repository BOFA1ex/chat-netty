package com.bofa.client.console;

import com.bofa.attribute.MessageType;
import com.bofa.attribute.UserStatus;
import com.bofa.client.util.ConsoleBuilder;
import com.bofa.client.util.PrintUtil;
import com.bofa.client.util.StringTokenUtil;
import com.bofa.entity.MessageInfo;
import com.bofa.entity.User;
import com.bofa.exception.ChatException;
import com.bofa.protocol.request.*;
import com.bofa.session.Session;
import com.bofa.util.LocalDateTimeUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.*;

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
        commandHashMap.put(ClientCommand.SEND, new MessageCommandHandler());
        commandHashMap.put(ClientCommand.STATUSL, new StatusLCommandHandler());
        commandHashMap.put(ClientCommand.STATUSC, new StatusCCommandHandler());
    }

    public static void execute(Channel channel, Scanner scanner) {
        System.out.print("input command: ");
        String command = scanner.nextLine();

        if (StringUtils.isEmpty(command)) {
            HELP.commandHandle(channel, scanner);
            return;
        }
        ClientCommand cmd = null;
        try {
            command = StringTokenUtil.merge(command, "-");
            cmd = ClientCommand.valueOf(command);
        } catch (Exception e) {
            ChatException.throwChatException("无法识别该指令[" + command + "]");
        }
        commandHashMap.get(cmd)
                .commandHandle(channel, scanner)
                .waitingForResp(cmd, ConsoleCommand.DEFAULT_TIMEOUT);
    }

    /**
     * HELP COMMAND
     *
     * @see com.bofa.client.console.ClientCommand
     */
    static class HelpCommandHandler implements ConsoleCommand {
        @Override
        public ConsoleCommand commandHandle(Channel channel, Scanner scanner) {
            for (ClientCommand c : ClientCommand.values()) {
                System.out.println(c.options);
            }
            return this;
        }
    }

    /**
     * LOGIN COMMAND
     *
     * @see com.bofa.client.console.ClientCommand
     */
    static class LoginCommandHandler implements ConsoleCommand {

        @Override
        public ConsoleCommand commandHandle(Channel channel, Scanner scanner) {
            Session session = SessionUtil.getSession(channel);
            String[] source = inputUserNameAndPwd(scanner);
            LoginRequestPacket requestPacket = new LoginRequestPacket();
            if (session != null) {
                ChatException.throwChatException("客户端已登录["
                        + session.getUser().getUserName() + "]");
            }
            requestPacket.setUserName(source[0]);
            requestPacket.setPassword(source[1]);
            InetSocketAddress address = (InetSocketAddress) channel.localAddress();
            requestPacket.setCommonIp(address.getHostString());
            channel.writeAndFlush(requestPacket);
            return this;
        }
    }

    /**
     * REGISTER COMMAND
     *
     * @see com.bofa.client.console.ClientCommand
     */
    static class RegisterCommandHandler implements ConsoleCommand {

        @Override
        public ConsoleCommand commandHandle(Channel channel, Scanner scanner) {
            String[] source = inputUserNameAndPwd(scanner);

            RegisterRequestPacket requestPacket = new RegisterRequestPacket();
            requestPacket.setUserName(source[0]);
            requestPacket.setPassword(source[1]);
            channel.writeAndFlush(requestPacket);
            return this;
        }
    }

    /**
     * LOGOUT COMMAND
     *
     * @see com.bofa.client.console.ClientCommand
     */
    static class LogoutCommandHandler implements ConsoleCommand {
        @Override
        public ConsoleCommand commandHandle(Channel channel, Scanner scanner) {
            if (!SessionUtil.hasLogin(channel)) {
                ChatException.throwChatException("注销失败，当前没有登录的账号");
            }
            User user = SessionUtil.getSession(channel).getUser();
            LogoutRequestPacket requestPacket = new LogoutRequestPacket();
            requestPacket.setUserId(user.getUserId());
            requestPacket.setStatus(UserStatus.OFFLINE.status);
            channel.writeAndFlush(requestPacket);
            return this;
        }
    }

    /**
     * SEND MESSAGE COMMAND
     *
     * @see com.bofa.client.console.ClientCommand
     */
    static class MessageCommandHandler implements ConsoleCommand {
        @Override
        public ConsoleCommand commandHandle(Channel channel, Scanner scanner) {
            if (!SessionUtil.hasLogin(channel)) {
                ChatException.throwChatException("发送失败，当前没有登录的账号");
            }
            System.out.print("input your friendNames: ");
            String[] friendNames = StringTokenUtil.split(scanner.nextLine(), " ,\r\t\n");
            System.out.print("input your message: ");
            String content = scanner.nextLine();
            String dateTime = LocalDateTimeUtil.now();
            for (String name : friendNames) {
                Session session = SessionUtil.getSession(channel);
                Integer userFriendId;
                int userId = session.getUser().getUserId();
                if ((userFriendId = session.findFriendIdByName(name)) == null) {
                    ChatException.throwChatException("找不到该好友[" + name + "]");
                }
                MessageRequestPacket requestPacket = new MessageRequestPacket();
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setContent(content);
                messageInfo.setDateTime(dateTime);
                messageInfo.setFromUserId(userId);
                messageInfo.setToUserId(userFriendId);
                messageInfo.setMessageType(MessageType.TEXT);
                requestPacket.setUserIds(new ArrayList<>(Arrays.asList(userId, userFriendId)));
                requestPacket.setMessageInfo(messageInfo);
                channel.writeAndFlush(requestPacket);
            }
            return this;
        }

        @Override
        public void waitingForResp(ClientCommand cmd, int timeout) {

        }
    }

    /**
     * STATUS -L COMMAND
     *
     * @see com.bofa.client.console.ClientCommand
     */
    static class StatusLCommandHandler implements ConsoleCommand {

        @Override
        public ConsoleCommand commandHandle(Channel channel, Scanner scanner) {
            if (!SessionUtil.hasLogin(channel)) {
                ChatException.throwChatException("查看用户状态失败，当前没有登录的账号");
            }
            User user = SessionUtil.getSession(channel).getUser();
            String status = UserStatus.findByStatus(user.getStatus());
            PrintUtil.println(user.getUserName(), "当前状态: " + status);
            return this;
        }

        @Override
        public void waitingForResp(ClientCommand cmd, int timeout) {

        }
    }

    /**
     * STATUS -C COMMAND
     *
     * @see com.bofa.client.console.ClientCommand
     */
    static class StatusCCommandHandler implements ConsoleCommand {

        @Override
        public ConsoleCommand commandHandle(Channel channel, Scanner scanner) {
            if (!SessionUtil.hasLogin(channel)) {
                ChatException.throwChatException("切换用户状态失败，当前没有登录的账号");
            }
            ChangeStatusRequestPacket requestPacket = new ChangeStatusRequestPacket();
            int status = Integer.valueOf(scanner.nextLine());
            if (StringUtils.isEmpty(UserStatus.findByStatus(status))) {
                ChatException.throwChatException("status 不符合规范 参见UserStatus" + Arrays.toString(UserStatus.values()));
            }
            requestPacket.setStatus(status);
            requestPacket.setUserId(SessionUtil.getSession(channel).getUser().getUserId());
            channel.writeAndFlush(requestPacket);
            return this;
        }

        @Override
        public void waitingForResp(ClientCommand cmd, int timeout) {

        }
    }

    private static String[] inputUserNameAndPwd(Scanner scanner) {
        String userName, password;
        System.out.print("input your username: ");

        userName = scanner.nextLine();
        if (StringUtils.isEmpty(userName)) {
            ChatException.throwChatException("userName不可为空");
        }

        password = new String(ConsoleBuilder.INSTANCE.readPassword("input your password: "));
        if (StringUtils.isEmpty(password)) {
            ChatException.throwChatException("password不可为空");
        }
        return new String[]{userName, password};
    }
}
