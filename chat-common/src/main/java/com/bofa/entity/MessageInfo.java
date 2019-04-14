package com.bofa.entity;

import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.entity
 * @date 2019/4/13
 */
@Data
public class MessageInfo {

    private String messageId;

    private Integer fromUserId;

    private Integer toUserId;

    private String content;

    private Integer messageType;

    private String dateTime;

    private String toUserName;
}
