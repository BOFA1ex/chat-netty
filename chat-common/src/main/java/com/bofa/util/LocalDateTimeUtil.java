package com.bofa.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.util
 * @date 2019/4/2
 */
public class LocalDateTimeUtil {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static String now(String pattern) {
        if (pattern == null || pattern.length() == 0){
            return now();
        }
        return "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern)) + "]";
    }

    public static String now() {
        return "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(DEFAULT_PATTERN)) + "]";
    }

}
