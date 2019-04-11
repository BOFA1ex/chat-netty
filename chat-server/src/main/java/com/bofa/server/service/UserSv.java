package com.bofa.server.service;

import com.bofa.entity.User;
import com.bofa.exception.ChatErrorCode;
import com.bofa.exception.ChatException;
import com.bofa.protocol.command.Command;
import com.bofa.protocol.request.LoginRequestPacket;
import com.bofa.protocol.request.LogoutRequestPacket;
import com.bofa.protocol.request.RegisterRequestPacket;
import com.bofa.protocol.response.LoginResponsePacket;
import com.bofa.protocol.response.LogoutResponsePacket;
import com.bofa.protocol.response.RegisterResponsePacket;
import com.bofa.server.util.HttpUtil;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.service
 * @date 2019/4/3
 */
public class UserSv extends BaseSv {

    public static LoginResponsePacket login(LoginRequestPacket request) {
        return post(Command.LOGIN_REQUEST.url, request, LoginResponsePacket.class);
    }

    public static RegisterResponsePacket register(RegisterRequestPacket request) {
        return post(Command.REGISTER_REQUEST.url, request, RegisterResponsePacket.class);
    }

    public static LogoutResponsePacket logout(LogoutRequestPacket request) {
        return post(Command.LOGOUT_REQUEST.url, request, LogoutResponsePacket.class);
    }

}
