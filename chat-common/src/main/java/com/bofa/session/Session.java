package com.bofa.session;

import com.bofa.attribute.UserStatus;
import com.bofa.custom.Observable;
import com.bofa.custom.Observer;
import com.bofa.entity.User;
import com.bofa.entity.UserFriend;
import com.bofa.entity.UserNotice;
import com.bofa.util.SessionUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.session
 * @date 2019/4/2
 */
@Data
public class Session {

    // TODO: 2019/5/31 观察者设计模式已通过单测，记得在本地测试看下
    // TODO: 2019/5/31 把注册，登录，单聊，好友验证，状态修改，查看通知，好友状态修改（删除、隐身） 功能都过一遍
    // TODO: 2019/5/31 群聊功能需要先设计下表结构和整体方案思路

    static final Logger logger = LoggerFactory.getLogger(Session.class);

    public Session(User user, List<UserFriend> friends) {
        this(user, friends, null);
    }

    public Session(User user, List<UserFriend> friends, List<UserNotice> userNotices) {
        this.user = user;
        this.friends = friends;
        this.userNotices = userNotices;
        println("------------ 开始注册观察者身份 -----------------");
        this.observed = new SessionObserved(this);
        println("------------ 观察者身份注册完毕 -----------------");
    }

    private User user;

    private List<UserFriend> friends;

    private List<UserNotice> userNotices;

    private UserFriend lastChatUserFriend;

    /**
     * 观察者身份
     */
    private SessionObserver observer;

    /**
     * 被观察者身份
     */
    private SessionObserved observed;


    public UserFriend findFriendByName(String name) {
        for (UserFriend uf : friends) {
            if (uf.getUserFriendName().equals(name)) {
                return uf;
            }
        }
        return null;
    }

    @Data
    public class SessionObserver implements Observer {

        /**
         * 观察者绑定本人SESSION信息
         */
        Session observerSession;

        public SessionObserver(Session session) {
            this.observerSession = session;
            session.setObserver(this);
        }

        /**
         * @param o   被观察者
         * @param arg Session 被观察者的session
         */
        @Override
        public void update(Observable o, Object arg) {
            /**
             * 被观察者的session
             */
            Session observedSession = (Session) arg;
            User user = observerSession.getUser();

            observerSession.getFriends().forEach(userFriend -> {
                if (userFriend.getUserFriendName().equals(user.getUserName())) {
                    userFriend.setStatus(user.getStatus());
                }
            });
            /**
             * 观察者根据其被观察者的身份获取观察者列表，删除传递的被观察者的观察者身份
             */
            observerSession.getObserved().deleteObserver(observedSession);
        }
    }

    /**
     * jdk 自带的Observable类，vector性能很差而且内部成员变量不透明
     * 放弃采用, 自定义接口observer和observable类(CopyOnWriteArrayList)
     */
    public class SessionObserved extends Observable {

        /**
         * 注册被观察者身份时，需要创建并填充观察者队列
         * 如果有在线好友，则加入观察者队列
         * 并且判断该好友是否已注册被观察者身份，如果已注册被观察身份，说明观察者队列已经填充完毕
         * 需要自己手动将自己作为观察者对象插入到该好友的观察者队列
         *
         * @param localSession
         */
        public SessionObserved(Session localSession) {
            localSession.getFriends().forEach(userFriend -> {
                Optional.ofNullable(SessionUtil.getSession(userFriend.getUserFriendId())).ifPresent(
                        s -> {
                            println("---------------------------");
                            println("[" + s.getUser().getUserName() + "]在线，加入观察者队列");
                            println("---------------------------");
                            addObserver(new SessionObserver(s));
                            SessionObserved so;
                            if ((so = s.getObserved()) != null) {
                                so.addObserver(new SessionObserver(localSession));
                            }
                        }
                );
            });
        }

    }

    private void println(String msg) {
        logger.debug("[" + user.getUserName() + "] " + msg);
    }


    /**
     * 登出，通知观察者更新被观察者的状态，并且移除观察者队列
     * {@link SessionObserver#update(Observable, Object)}
     */
    public void notifyLogout() {
        println("用户登出，通知观察者队列更新session，并移除观察者队列");
        observed.setChanged();
        observed.notifyObservers(this);
        println("移除观察者队列");
        observed.clearObservers();
    }


    @Override
    public String toString() {
        return "Session{" +
                "user='" + user + '\'' +
                ", friendNames=" + friends +
                '}';
    }
}
