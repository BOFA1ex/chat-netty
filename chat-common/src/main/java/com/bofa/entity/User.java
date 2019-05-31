package com.bofa.entity;

import com.ai.nbs.common.spring.dao.entity.MybatisEntity;
import com.bofa.attribute.UserStatus;
import com.bofa.util.LocalDateTimeUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.dto
 * @date 2019/4/10
 */
@Data
public class User extends MybatisEntity implements Serializable {

    private Integer userId;

    private String userName;

    private Integer status;

    @Override
    public String toString() {
        return "用户信息" + '\n' +
                ">> 名字 " + userName + '\n' +
                ">> 状态 " + UserStatus.findByStatus(status, true);
    }
}
