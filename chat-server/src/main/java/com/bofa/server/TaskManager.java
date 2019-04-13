package com.bofa.server;

import com.bofa.server.util.LoggerUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server
 * @date 2019/4/11
 */
public class TaskManager {

    private static final ThreadPoolExecutor BUSI_POOL;
    static final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    static {
        int coreSize = Runtime.getRuntime().availableProcessors();
        BUSI_POOL = new ThreadPoolExecutor(coreSize, coreSize * 2, 0L,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
                new ThreadFactoryBuilder().setNameFormat("busi-worker-%d").build());
    }

    public static void execute(String taskName, Runnable runnable) {
        LoggerUtil.debug(logger, "Task", taskName.toUpperCase());
        BUSI_POOL.execute(runnable);
    }
}
