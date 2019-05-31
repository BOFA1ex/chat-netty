package com.bofa.server.service;

import com.bofa.attribute.UserStatus;
import com.bofa.entity.User;
import com.bofa.protocol.command.Command;
import com.bofa.protocol.request.*;
import com.bofa.protocol.response.*;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.service
 * @date 2019/4/3
 */
public class UserSv extends BaseSv {

    public static LoginResponsePacket login(LoginRequestPacket request) {
        LoginResponsePacket response = post(Command.LOGIN_REQUEST.url, request, LoginResponsePacket.class);
        if (!response.isSuccess()){
            User user = new User();
            user.setUserName(request.getUserName());
            user.setStatus(UserStatus.OFFLINE.status);
            response.setUser(user);
        }
        return response;
    }

    public static RegisterResponsePacket register(RegisterRequestPacket request) {
        return post(Command.REGISTER_REQUEST.url, request, RegisterResponsePacket.class);
    }

    public static LogoutResponsePacket logout(LogoutRequestPacket request) {
        return post(Command.LOGOUT_REQUEST.url, request, LogoutResponsePacket.class);
    }

    public static ChangeStatusResponsePacket changeStatus(ChangeStatusRequestPacket request) {
        return post(Command.CHANGE_STATUS_REQUEST.url, request, ChangeStatusResponsePacket.class);
    }
}
