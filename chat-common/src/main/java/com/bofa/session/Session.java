package com.bofa.session;

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

    public Session(Integer userId, String userName) {
        this.userName = userName;
        this.userId = userId;
    }
    private Integer userId;

    private String userName;

    private Integer status;

    private List<String> friendIds;

    @Override
    public String toString() {
        return "Session{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", groupUserIds=" + friendIds +
                '}';
    }
}
