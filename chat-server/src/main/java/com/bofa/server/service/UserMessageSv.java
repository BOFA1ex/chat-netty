package com.bofa.server.service;

import com.bofa.entity.UserNotice;
import com.bofa.protocol.command.Command;
import com.bofa.protocol.request.MessageRequestPacket;
import com.bofa.protocol.response.AbstractResponsePacket;
import com.bofa.protocol.response.MessageResponsePacket;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.server.service
 * @date 2019/4/16
 */
public class UserMessageSv extends BaseSv {

    public static AbstractResponsePacket offlineMessage(UserNotice request) {
        return post(Command.OFFLINE_MESSAGE.url, request, AbstractResponsePacket.DefaultAbstractResponsePacket.class);
    }

    public static MessageResponsePacket message(MessageRequestPacket request) {
        return post(Command.MESSAGE_REQUEST.url, request, MessageResponsePacket.class);
    }
}
