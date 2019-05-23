package com.bofa.client.dao.userfriends;

import com.ai.nbs.common.spring.dao.BaseMapper;
import com.bofa.client.dao.userfriends.entity.UserFriendsExample;
import com.bofa.entity.UserFriend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserFriendsMapper extends BaseMapper<UserFriend, Integer, UserFriendsExample> {

    void deleteAll(Integer userId);

    void insertAll(List<UserFriend> list);
}