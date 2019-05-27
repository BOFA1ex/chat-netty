package com.bofa.client.dao.userNotice;

import com.ai.nbs.common.spring.dao.BaseMapper;
import com.bofa.client.dao.userNotice.entity.UserNoticeExample;
import com.bofa.entity.UserNotice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserNoticeMapper extends BaseMapper<UserNotice, String, UserNoticeExample> {

    void saveAll(List<UserNotice> list);

    /**
     * h2-database 不支持批量更新
     * org.springframework.jdbc.BadSqlGrammarException
     * org.h2.jdbc.JdbcSQLException : Syntax error in SQL statement
     * 支持批量插入，不支持批量更新，简直是设计鬼才
     * @param list
     */
    void updateAll(List<UserNotice> list);

    Integer selectMaxId();
}