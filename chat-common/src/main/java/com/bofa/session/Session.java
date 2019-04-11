package com.bofa.session;

import com.bofa.entity.User;
import com.bofa.entity.UserFriend;
import lombok.Data;

import java.util.List;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.session
 * @date 2019/4/2
 */
@Data
public class Session {

    public Session(User user, List<UserFriend> friends) {
        this.user = user;
        this.friends = friends;
    }

    private User user;

    private List<UserFriend> friends;

    @Override
    public String toString() {
        return "Session{" +
                "user='" + user + '\'' +
                ", friendNames=" + friends +
                '}';
    }
}
