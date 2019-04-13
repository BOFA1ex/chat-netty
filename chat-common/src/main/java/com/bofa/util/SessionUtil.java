package com.bofa.util;

import com.bofa.attribute.Attributes;
import com.bofa.exception.ChatErrorCode;
import com.bofa.exception.ChatException;
import com.bofa.session.Session;
import io.netty.channel.Channel;
import java.util.Map;
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
    private static CyclicBarrier respOrder = new CyclicBarrier(2);

    public static void bindSession(Session session, Channel channel) {
        userIdChannelMap.put(session.getUser().getUserId(), channel);
        channel.attr(Attributes.SESSION).set(session);
    }

    public static void unbindSession(Channel channel) {
        if (hasLogin(channel)) {
            userIdChannelMap.remove(getSession(channel).getUser().getUserId());
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
        return getSession(getChannel(userId));
    }

    public static Channel getChannel(Integer userId) {
        return userIdChannelMap.get(userId);
    }

    public static void waitingForResp(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, BrokenBarrierException {
        System.out.println("[" + currentThread().getName() + "] waitingForResp");
        respOrder.await(timeout, unit);
    }

    public static void signalRespOrder() throws BrokenBarrierException, InterruptedException {
        System.out.println("[" + currentThread().getName() + "] signalRespOrder");
        if (!respOrder.isBroken() && respOrder.getNumberWaiting() == 1){
            respOrder.await();
            respOrder.reset();
        }
    }

    public static void resetRespOrder() {
        System.out.println("[" + currentThread().getName() + "] resetRespOrder");
        if (respOrder.isBroken()){
            respOrder.reset();
        }
    }

    private static boolean isBorkenRespOrder(){
        return respOrder.isBroken();
    }
}
