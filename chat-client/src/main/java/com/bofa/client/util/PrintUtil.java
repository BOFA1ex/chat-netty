package com.bofa.client.util;

import com.bofa.util.LocalDateTimeUtil;

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
}
