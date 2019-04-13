package com.bofa.client.console;

import com.bofa.protocol.command.Command;
import lombok.Data;

import java.util.Arrays;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.StringTokenizer;
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

    STATUSL("status", new Option("status -l", "查看当前状态")),

    STATUSC("status", new Option("status -c [arg]", "切换当前状态 1:在线 2:离线 3:隐身")),

    FRIENDL("friend", new Option("friend -l", "查看当前在线好友")),

    FRIENDLA("friend", new Option("friend -la", "查看所有好友")),

    FRIENDA("friend", new Option("friend -a [username]", "添加好友")),

    FRIENDR("friend", new Option("friend -r [username]", "删除好友")),

    SEND("send", new Option("send [userName1, userName2, ...]", "指定好友发送信息")),

    NOTICEL("notice", new Option("notice -l", "查看通知")),

    LOGOUT("logout", new Option("logout", "注销当前账号"));

    ClientCommand(String command, Option options) {
        this.command = command;
        this.options = options;
    }

    String command;

    Option options;

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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Pattern pattern = Pattern.compile("\\d");
        while (!scanner.hasNext(pattern)) {
            System.out.println(scanner.next());
        }
        System.out.println("status: " + scanner.next());
    }
}
