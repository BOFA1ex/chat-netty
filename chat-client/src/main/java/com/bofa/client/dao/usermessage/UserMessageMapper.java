package com.bofa.client.dao.usermessage;

import com.ai.nbs.common.spring.dao.BaseMapper;
import com.bofa.client.dao.usermessage.entity.UserMessageExample;
import com.bofa.entity.UserMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMessageMapper extends BaseMapper<UserMessage, Integer, UserMessageExample> {
}