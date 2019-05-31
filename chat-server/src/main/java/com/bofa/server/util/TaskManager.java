package com.bofa.server.util;

import com.bofa.attribute.NoticeStatus;
import com.bofa.attribute.NoticeType;
import com.bofa.entity.User;
import com.bofa.entity.UserNotice;
import com.bofa.exception.ChatException;
import com.bofa.protocol.command.Command;
import com.bofa.protocol.response.AbstractResponsePacket;
import com.bofa.protocol.response.NoticeResponsePacket;
import com.bofa.util.LocalDateTimeUtil;
import com.bofa.util.SessionUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server
 * @date 2019/4/11
 */
public class TaskManager {

    private static final ThreadPoolExecutor BUSI_POOL;
    private static final int DEFAULT_RETRY = 5;
    private static final int DEFAULT_MAP_CAPACITY = 64;
    private static final ConcurrentHashMap<String, TopicTask> CONCURRENT_HASH_MAP = new ConcurrentHashMap<>(DEFAULT_MAP_CAPACITY);
    static final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    static {
        int coreSize = Runtime.getRuntime().availableProcessors();
        BUSI_POOL = new ThreadPoolExecutor(coreSize, coreSize * 2, 0L,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
                new ThreadFactoryBuilder().setNameFormat("busi-worker-%d").build());
    }

    public static void execute(String taskName, Callable<? extends AbstractResponsePacket> callable) {
        assert taskName != null : "taskName 不可为空";
        assert callable != null : "callable 不可为空";
        LoggerUtil.debug(logger, "ScheduleTask", taskName.toUpperCase());
        Future<? extends AbstractResponsePacket> future = BUSI_POOL.submit(callable);
        AbstractResponsePacket response = null;
        int retry = DEFAULT_RETRY;
        try {
            while (retry-- > 0 && (response = future.get()).isConnectionError()) {
                LoggerUtil.debug(logger, "ScheduleTask Retry REST " + retry + " TIMES", taskName.toUpperCase());
                future = BUSI_POOL.submit(callable);
            }
        } catch (ExecutionException | InterruptedException e) {
            LoggerUtil.debug(logger, "ScheduleTask throws ", e.getMessage());
        } finally {
            if (response == null) {
                throw new Error("Connection refused");
            }
            if (retry <= 0) {
                throw new Error(response.getMessage());
            }
        }
    }

    public static void execute(String taskName, Callable<? extends AbstractResponsePacket> callable, Consumer<AbstractResponsePacket> andThen) {
        assert taskName != null : "taskName 不可为空";
        assert callable != null : "callable 不可为空";
        assert andThen != null : "andThen 不可为空";
        LoggerUtil.debug(logger, "ScheduleTask", taskName.toUpperCase());
        Future<? extends AbstractResponsePacket> future = BUSI_POOL.submit(callable);
        AbstractResponsePacket response = null;
        int retry = DEFAULT_RETRY;
        try {
            while (retry-- > 0 && (response = future.get()).isConnectionError()) {
                LoggerUtil.debug(logger, "ScheduleTask Retry REST " + retry + " TIMES", taskName.toUpperCase());
                future = BUSI_POOL.submit(callable);
            }
        } catch (ExecutionException | InterruptedException e) {
            LoggerUtil.debug(logger, "ScheduleTask throws ", e.getMessage());
        } finally {
            LoggerUtil.debug(logger, "ScheduleTask invoke writeAndFlush", andThen.toString());
            andThen.accept(response);
            if (response == null) {
                throw new Error("Connection refused");
            }
            if (retry <= 0) {
                throw new Error(response.getMessage());
            }
        }
    }


    public static void execute(String taskName, Runnable runnable) {
        assert taskName != null : "taskName 不可为空";
        assert runnable != null : "runnable 不可为空";
        LoggerUtil.debug(logger, "Task", taskName.toUpperCase());
        BUSI_POOL.execute(runnable);
    }

    public static void topicExecute(String topic, String taskName, Callable<? extends AbstractResponsePacket> callable,
                                    @NotNull Channel channel,
                                    boolean execute)
            throws ExecutionException, InterruptedException {
        topicExecute(topic, taskName, callable, null, channel, execute);
    }

    public static void topicExecute(String topic, String taskName, Callable<? extends AbstractResponsePacket> callable,
                                    @Nullable Consumer<AbstractResponsePacket> andThen, @NotNull Channel channel, boolean execute) {
        assert taskName != null : "taskName 不可为空";
        assert topic != null : "topic 不可为空";
        assert callable != null : "callable 不可为空";
        LoggerUtil.debug(logger, "Topic", topic.toUpperCase() + " " + taskName.toUpperCase());
        TopicTask topicTask = CONCURRENT_HASH_MAP.get(topic);
        if (topicTask == null) {
            CONCURRENT_HASH_MAP.put(topic, new TopicTask(null, callable, taskName, andThen, channel));
        } else {
            topicTask.addLast(taskName, callable, andThen);
        }
        topicExecute0(topic, execute);
    }

    public static void topicExecute(String topic, String taskName, Runnable runnable, @NotNull Channel channel, boolean execute) {
        assert topic != null : "topic不可为空";
        assert taskName != null : "taskName不可为空";
        assert runnable != null : "runnable不可为空";
        LoggerUtil.debug(logger, "Topic", topic.toUpperCase() + " " + taskName.toUpperCase());
        TopicTask topicTask = CONCURRENT_HASH_MAP.get(topic);
        if (topicTask == null) {
            CONCURRENT_HASH_MAP.put(topic, new TopicTask(null, runnable, taskName, channel));
        } else {
            topicTask.addLast(taskName, runnable);
        }
        topicExecute0(topic, execute);
    }

    private static void topicExecute0(String topic, boolean execute) {
        if (execute) {
            TopicTask task = CONCURRENT_HASH_MAP.get(topic);
            assert task.channel != null;
            Channel channel = task.channel;
            Throwable t = null;
            while (task != null) {
                try {
                    if (task.runnable != null) {
                        execute(task.taskName, task.runnable);
                    } else if (task.andThen == null) {
                        execute(task.taskName, task.callable);
                    } else {
                        execute(task.taskName, task.callable, task.andThen);
                    }
                    task = task.next;
                } catch (Error e) {
                    // break loop and remove topic
                    LoggerUtil.debug(logger, "Topic Error", e.getMessage());
                    t = e;
                    break;
                }
            }
            Optional.ofNullable(t).ifPresent(handleError(topic, channel));
            LoggerUtil.debug(logger, "Topic Removed", topic.toUpperCase());
            CONCURRENT_HASH_MAP.remove(topic);
        }
    }

    /**
     * handle when task error which mapper notice to write and flush the client by channel.
     * @param channel
     * @return
     */
    private static Consumer<? super Throwable> handleError(String topic, Channel channel) {
        String SYSTEM_NAME = "SYSTEM";
        int SYSTEM_ID = -1;
        return t -> {
            UserNotice un = new UserNotice();
            User user = SessionUtil.getSession(channel).getUser();
            un.setNoticeid(user.getUserId());
            un.setNoticename(user.getUserName());
            un.setNoticecontent(topic + "\n" +t.getMessage());
            un.setNoticedatetime(LocalDateTimeUtil.now0());
            un.setUsername(SYSTEM_NAME);
            un.setUserid(SYSTEM_ID);
            un.setNoticestatus(NoticeStatus.UNREAD.status);
            un.setNoticetype(NoticeType.INTERNAL_SYSTEM_ERROR.type);
            channel.writeAndFlush(new NoticeResponsePacket(un)).addListener(future -> {
                if (!future.isSuccess()){
                    future.cause().printStackTrace();
                }
            });
        };
    }

    static class TopicTask {
        TopicTask next;
        Callable<? extends AbstractResponsePacket> callable;
        Runnable runnable;
        String taskName;
        Consumer<AbstractResponsePacket> andThen;
        Channel channel;

        TopicTask(TopicTask next, Callable<? extends AbstractResponsePacket> callable, String taskName, Consumer<AbstractResponsePacket> andThen, Channel channel) {
            this.next = next;
            this.callable = callable;
            this.taskName = taskName;
            this.andThen = andThen;
            this.channel = channel;
        }

        public TopicTask(TopicTask next, Runnable runnable, String taskName, Channel channel) {
            this.next = next;
            this.runnable = runnable;
            this.taskName = taskName;
            this.channel = channel;
        }

        public TopicTask(TopicTask next, Callable<? extends AbstractResponsePacket> callable, String taskName, Consumer<AbstractResponsePacket> andThen) {
            this(next, callable, taskName, andThen, null);
        }

        public TopicTask(TopicTask next, Runnable runnable, String taskName) {
            this(next, runnable, taskName, null);
        }

        void addLast(String taskName, Callable<? extends AbstractResponsePacket> callable, @Nullable Consumer<AbstractResponsePacket> andThen) {
            TopicTask newNode;
            TopicTask temp = this;
            while (temp.next != null) {
                temp = next;
            }
            newNode = new TopicTask(null, callable, taskName, andThen);
            temp.next = newNode;
        }

        void addLast(String taskName, Runnable runnable) {
            TopicTask newNode;
            TopicTask temp = this;
            while (temp.next != null) {
                temp = next;
            }
            newNode = new TopicTask(null, runnable, taskName);
            temp.next = newNode;
        }
    }
}
