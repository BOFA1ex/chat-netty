package com.bofa.client.console;

import com.bofa.protocol.command.Command;
import lombok.Data;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.console
 * @date 2019/4/6
 */
public enum ClientCommand {

    /**
     * command [-options] [args...]
     * Sample: login -s 3 隐身登录上线
     * friend -l 查看当前在线用户
     * friend -la 查看所有用户
     * ...
     */
    HELP("help", new Option("help", "查看帮助")),

    LOGIN("login", new Option("login", "登录")),

    REGISTER("register", new Option("register", "注册")),

    STATUS("status", new Option("status -l", "查看当前状态"), new Option("status -c [arg]", "切换当前状态 1:在线 2:离线 3:隐身")),

    STATUSH("status"),

    STATUSL("status"),

    STATUSC("status"),

    FRIEND("friend", new Option("friend", "查看指定好友信息(最近聊天时间，备注，状态)"), new

            Option("friend -f [userId or regex match userName]", "查找好友"), new

            Option("friend -l", "查看当前在线好友"), new

            Option("friend -la", "查看所有好友"), new

            Option("friend -a [username]", "添加好友"), new

            Option("friend -c [username]", "修改与好友的关系(对其隐身或者删除好友关系)"), new

            Option("friend -rc [username]", "修改好友备注")),

    FRIENDL("friend"),

    FRIENDLA("friend"),

    FRIENDA("friend"),

    FRIENDC("friend"),

    FRIENDRC("friend"),

    FRIENDF("friend"),

    SEND("send", new Option("send [userName1, userName2, ...]", "指定好友发送信息")),

    MESSAGE("message", new Option("message [userName]", "显示与该好友的聊天记录")),

    GROUP_MESSAGE("group message", new Option("group message [groupName]", "显示该群聊的聊天记录")),

    NOTICE("notice", new Option("notice -l", "查看未读通知"), new Option("notice -la", "查看所有通知")),

    NOTICEL("notice"),

    NOTICELA("notice"),

    LOGOUT("logout", new Option("logout", "注销当前账号"));

    ClientCommand(String command, Option... options) {
        this.command = command;
        this.options = options;
    }

    String command;

    Option[] options;

    public Option[] getOptions() {
        return options;
    }

    static class Option {

        public Option(String option, String comment) {
            this.comment = comment;
            this.option = option;
        }

        String comment;

        String option;

        @Override
        public String toString() {
            return option + " " + comment;
        }
    }


    public static String returnAllCommand() {
        StringBuilder sb = new StringBuilder();
        for (ClientCommand c : ClientCommand.values()) {
            int k = 0;
            boolean flag = false;
            for (Option option : c.options) {
                sb.append("[");
                sb.append(option);
                sb.append("]").append(" ");
                if (++k % 2 == 0) {
                    sb.append("\n");
                    flag = true;
                }
            }
            if (c.options.length != 0 && !flag) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(returnAllCommand());
    }

}
