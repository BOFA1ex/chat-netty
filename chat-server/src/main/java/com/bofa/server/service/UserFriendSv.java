package com.bofa.server.service;

import com.bofa.entity.UserFriend;
import com.bofa.protocol.command.Command;
import com.bofa.protocol.request.FriendARequestPacket;
import com.bofa.protocol.request.FriendCRequestPacket;
import com.bofa.protocol.response.AbstractResponsePacket;
import com.bofa.protocol.response.FriendACallBackResponsePacket;
import com.bofa.protocol.response.FriendAResponsePacket;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.service
 * @date 2019/4/10
 */
public class UserFriendSv extends BaseSv {

    public static AbstractResponsePacket submitApproval(FriendARequestPacket request) {
        return post(Command.FRIENDA_REQUEST.url, request, FriendAResponsePacket.class);
    }

    public static AbstractResponsePacket updateApproval(FriendARequestPacket request) {
        return post(Command.FRIENDA_CALLBACK_RESPONSE.url, request, AbstractResponsePacket.DefaultAbstractResponsePacket.class);
    }

    public static AbstractResponsePacket changeStatus(FriendCRequestPacket request) {
        return post(Command.FRIENDC_REQUEST.url, request, AbstractResponsePacket.DefaultAbstractResponsePacket.class);
    }

    public static AbstractResponsePacket saveUserFriend(UserFriend request) {
        return post(Command.SAVE_FRIEND.url, request, AbstractResponsePacket.DefaultAbstractResponsePacket.class);
    }
}
