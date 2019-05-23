package com.bofa.session;

import com.bofa.entity.User;
import com.bofa.entity.UserFriend;
import com.bofa.entity.UserNotice;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.session
 * @date 2019/4/2
 */
@Data
public class Session {

    public Session(User user, List<UserFriend> friends, List<UserNotice> userNotices) {
        this.user = user;
        this.friends = friends;
        this.userNotices = userNotices;
    }
    public Session(User user, List<UserFriend> friends) {
        this.user = user;
        this.friends = friends;
    }

    private User user;

    private List<UserFriend> friends;

    private List<UserNotice> userNotices;

    private UserFriend lastChatUserFriend;

    public UserFriend findFriendByName(String name){
        for (UserFriend uf : friends) {
            if (uf.getUserName().equals(name)) {
                return uf;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Session{" +
                "user='" + user + '\'' +
                ", friendNames=" + friends +
                '}';
    }
}
