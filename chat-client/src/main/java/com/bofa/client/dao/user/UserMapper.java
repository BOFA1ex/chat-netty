package com.bofa.client.dao.user;

import com.ai.nbs.common.spring.dao.BaseMapper;
import com.bofa.client.dao.user.entity.UserExample;
import com.bofa.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User, Integer, UserExample> {

}