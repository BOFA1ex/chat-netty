package com.bofa.util;

import com.bofa.attribute.Attributes;
import com.bofa.attribute.UserStatus;
import com.bofa.entity.User;
import com.bofa.exception.ChatErrorCode;
import com.bofa.exception.ChatException;
import com.bofa.session.Session;
import io.netty.channel.Channel;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.*;

import static java.lang.Thread.currentThread;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.util
 * @date 2019/4/2
 */
public class SessionUtil {

    private static Map<Integer, Channel> userIdChannelMap = new ConcurrentHashMap<>();
    private static Map<Integer, User> userMap = new ConcurrentHashMap<>();
    private static CyclicBarrier respOrder = new CyclicBarrier(2);

    public static void bindSession(Session session, Channel channel) {
        User user = session.getUser();
        userIdChannelMap.put(user.getUserId(), channel);
        userMap.put(user.getUserId(), user);
        channel.attr(Attributes.SESSION).set(session);
    }

    public static void unbindSession(Channel channel) {
        if ((getSession(channel)) != null) {
            Session session = getSession(channel);
            Integer userId = session.getUser().getUserId();
            userIdChannelMap.remove(userId);
            userMap.remove(userId);
            /**
             * 通知观察者，被观察者登出
             */
            session.notifyLogout();
            channel.attr(Attributes.SESSION).set(null);
        } else {
            ChatException.throwChatException(ChatErrorCode.UNAUTHORIZED, "请重新登录");
        }
    }

    public static boolean hasLogin(Channel channel) {
        return channel.attr(Attributes.SESSION).get() != null;
    }

    public static Session getSession(Channel channel) {
        return channel.attr(Attributes.SESSION).get();
    }

    public static Session getSession(Integer userId) {
        Channel channel = getChannel(userId);
        if (channel != null) {
            return getSession(channel);
        }
        return null;
    }

    public static Session getSession(String userName) {
        return getSession(getUser(userName).getUserId());
    }

    public static Channel getChannel(Integer userId) {
        return userIdChannelMap.get(userId);
    }

    public static User getUser(Integer userId) {
        return userMap.get(userId);
    }

    public static User getUser(String userName) {
        return userMap.values().stream()
                .filter(user -> user.getUserName().equals(userName))
                .findFirst().orElse(null);
    }

    public static void waitingForResp() throws InterruptedException, BrokenBarrierException {
        PrintUtil.println("正在等待响应...");
        respOrder.await();
    }

    public static void signalRespOrder() throws BrokenBarrierException, InterruptedException {
        if (!respOrder.isBroken() && respOrder.getNumberWaiting() == 1) {
            respOrder.await();
            respOrder.reset();
        }
    }

    public static void resetRespOrder() {
        if (respOrder.isBroken()) {
            respOrder.reset();
        }
    }

}
