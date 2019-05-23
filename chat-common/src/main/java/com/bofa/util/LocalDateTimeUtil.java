package com.bofa.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.util
 * @date 2019/4/2
 */
public class LocalDateTimeUtil {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final String DEFAULT_DAY_PATTERN = "yyyy-MM-dd";

    private static ZoneId localZoneId = ZoneId.of("Asia/Shanghai");


    public static String now(String pattern) {
        if (pattern == null || pattern.length() == 0) {
            return now();
        }
        return "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern)) + "]";
    }

    public static String now() {
        return "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(DEFAULT_PATTERN)) + "]";
    }

    public static String now0() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
    }

    /**
     * 命令真的很痛苦..
     */
    public static String now1() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DEFAULT_DAY_PATTERN));
    }

    public static String instantConvert2Time(Instant instant) {
        return LocalDateTime.ofInstant(instant, localZoneId).
                format(DateTimeFormatter.ofPattern(DEFAULT_DAY_PATTERN));
    }
}
