package com.bofa.client.util;

import com.bofa.exception.ChatErrorCode;
import com.bofa.exception.ChatException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.util
 * @date 2019/4/26
 */
public class PrintStreamDelegate {

    private static Scanner sc = new Scanner(System.in);

    private static ThreadPoolExecutor executor =
            new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, new LinkedBlockingDeque<>()
                    , new ThreadFactoryBuilder().setNameFormat("printStream-worker")
                    .build());

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> sc.close()));
    }

    public static String nextLine() {
        String s = null;
        try {
            s = sc.nextLine();
        } catch (Exception e) {
            ChatException.throwChatException(ChatErrorCode.CLIENT_CLOSE);
        }
        return s;
    }

    public static String next() {
        return sc.next();
    }

    public static void delegate(Runnable runnable) {
        assert runnable != null;
        executor.execute(runnable);
    }

}
