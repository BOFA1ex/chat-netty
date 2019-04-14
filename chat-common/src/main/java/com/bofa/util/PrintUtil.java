package com.bofa.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.client.util
 * @date 2019/4/11
 */
public class PrintUtil {

    public static void print(String name, String eventMessage) {
        System.out.print(LocalDateTimeUtil.now() + " [" + name + "]" + eventMessage);
    }

    public static void println(String name, String eventMessage) {
        System.out.println(LocalDateTimeUtil.now() + " [" + name + "]" + eventMessage);
    }

    public static void print(String eventMessage) {
        System.out.print(LocalDateTimeUtil.now() + " " + eventMessage);
    }
    public static void println(String eventMessage) {
        System.out.println(LocalDateTimeUtil.now() + " " + eventMessage);
    }
}
