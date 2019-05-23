package com.bofa.client.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.util
 * @date 2019/4/20
 */
public class H2TaskManager {

    private static final ThreadPoolExecutor BUSI_POOL;
    static final Logger logger = LoggerFactory.getLogger(H2TaskManager.class);

    static {
        int coreSize = Runtime.getRuntime().availableProcessors();
        BUSI_POOL = new ThreadPoolExecutor(coreSize, coreSize * 2, 0L,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
                new ThreadFactoryBuilder().setNameFormat("h2-busi-worker-%d").build());
    }
//
//    public static void execute(String taskName, Callable<AbstractResponsePacket> callable) {
//        logger.debug("ScheduleTask " + taskName.toUpperCase());
//        Future<? extends AbstractResponsePacket> future = BUSI_POOL.submit(callable);
//        if (future.isDone()) {
//            try {
//                while (!future.get().isSuccess()) {
//                    logger.debug("ScheduleTask Retry" + taskName.toUpperCase());
//                    BUSI_POOL.submit(callable);
//                }
//            } catch (InterruptedException | ExecutionException e) {
//                logger.error("ScheduleTask throw " + e.getCause(), e.getMessage());
//                BUSI_POOL.submit(callable);
//            }
//        }
//    }

    public static void execute(String taskName, Runnable runnable) {
        logger.debug("Task", taskName.toUpperCase());
        BUSI_POOL.execute(runnable);
    }
}
