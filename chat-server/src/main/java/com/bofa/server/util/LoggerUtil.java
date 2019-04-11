package com.bofa.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.util
 * @date 2019/4/11
 */
public class LoggerUtil {

    public static void info(Logger logger, String name, String eventMessage) {
        logger.info("[" + name + "] " + eventMessage);
    }

    public static void debug(Logger logger, String name, String eventMessage) {
        logger.debug("[" + name + "] " + eventMessage);
    }

    public static void error(Logger logger, String name, String eventMessage) {
        logger.error("[" + name + "] " + eventMessage);
    }

    public static void main(String[] args) {
        LoggerUtil.error(LoggerFactory.getLogger(LoggerUtil.class), "bofa", "login success");
    }

}
