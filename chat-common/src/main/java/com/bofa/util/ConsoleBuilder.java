package com.bofa.util;

import java.io.Console;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.util
 * @date 2019/4/11
 */
public class ConsoleBuilder {

    public static final Console INSTANCE = getConsole();

    private static Console getConsole() {
        Console cons = System.console();
        if (cons == null) {
            System.out.println("Couldn't get Console instance, maybe you're running this from within an IDE?");
        }
        return cons;
    }
}
