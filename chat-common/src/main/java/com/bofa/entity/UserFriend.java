package com.bofa.entity;

import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.dto
 * @date 2019/4/10
 */
@Data
public class UserFriend extends User{

    private String lastChatTime;

    private String remark;
}
