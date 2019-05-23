package com.bofa.client.service;

import com.ai.nbs.common.spring.service.BaseSv;
import com.bofa.client.dao.user.UserMapper;
import com.bofa.client.dao.user.entity.UserExample;
import com.bofa.entity.User;
import com.bofa.entity.UserFriend;
import com.bofa.entity.UserNotice;
import com.bofa.exception.ChatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.service
 * @date 2019/4/20
 */
@Service
public class UserSv extends BaseSv<UserMapper, User, Integer, UserExample> {

    /**
     * save lastestUserId when user login but the process has not exit yet.
     */
    private Integer latestUserId;

    @Autowired
    private UserFriendSv userFriendSv;

    @Autowired
    private UserNoticeSv userNoticeSv;

    /**
     * if user has cached update : insert
     *
     * @param user
     */
    public void save(User user, List<UserFriend> userFriends) {
        User record = super.mapper.selectByPrimaryKey(user.getUserId());
        if (record != null) {
            super.mapper.updateByPrimaryKey(user);
            return;
        }
        super.mapper.insert(user);
        userFriendSv.save(user.getUserId(), userFriends);
    }

    public User getUser() {
        if (latestUserId == null) {
            ChatException.throwChatException("No logged-in user exists");
        }
        return super.mapper.selectByPrimaryKey(latestUserId);
    }

    public List<UserFriend> getUserFriends() {
        if (latestUserId == null) {
            ChatException.throwChatException("No logged-in user exists");
        }
        return userFriendSv.getUserFriends(latestUserId);
    }

    public List<UserNotice> getUserNotices() {
        if (latestUserId == null) {
            ChatException.throwChatException("No logged-in user exists");
        }
        return userNoticeSv.getUserNotices(latestUserId);
    }

    public List<UserNotice> getUserNonReadNotices() {
        if (latestUserId == null) {
            ChatException.throwChatException("No logged-in user exists");
        }
        return userNoticeSv.getUserNonReadNotices(latestUserId);
    }

    public void setLatestUserId(Integer latestUserId) {
        this.latestUserId = latestUserId;
    }
}
