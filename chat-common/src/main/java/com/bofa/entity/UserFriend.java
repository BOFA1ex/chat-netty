package com.bofa.entity;

import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.dto
 * @date 2019/4/10
 */
@Data
public class UserFriend extends User {

    private Integer id;

    private Integer userFriendId;

    private String userFriendName;

    private String lastChatTime;

    private String remark;

    @Override
    public String toString() {
        return "好友信息" + '\n' +
                ">> 名字 " + userFriendName + '\n' +
                ">> 备注 " + remark + '\n' +
                ">> 最近聊天时间 " + lastChatTime ;
    }
}
