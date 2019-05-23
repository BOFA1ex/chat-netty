package com.bofa.client.dao.userNotice;

import com.ai.nbs.common.spring.dao.BaseMapper;
import com.bofa.client.dao.userNotice.entity.UserNoticeExample;
import com.bofa.entity.UserNotice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserNoticeMapper extends BaseMapper<UserNotice, String, UserNoticeExample> {

    void saveAll(List<UserNotice> list);

    void updateAll(List<UserNotice> list);

    Integer selectMaxId();
}