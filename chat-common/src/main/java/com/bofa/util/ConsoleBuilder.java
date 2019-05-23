package com.bofa.util;

import com.bofa.exception.ChatException;

import java.io.Console;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.util
 * @date 2019/4/11
 */
public class ConsoleBuilder {

    public static Console getConsole(){
        Console cons = System.console();
        if (cons == null) {
            ChatException.throwChatException("Couldn't get Console instance, maybe you're running this from within an IDE?");
        }
        return cons;
    }
}
