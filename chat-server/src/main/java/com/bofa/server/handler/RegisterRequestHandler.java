package com.bofa.server.handler;

import com.bofa.entity.User;
import com.bofa.entity.UserFriend;
import com.bofa.protocol.request.RegisterRequestPacket;
import com.bofa.protocol.response.RegisterResponsePacket;
import com.bofa.server.service.UserSv;
import com.bofa.server.util.LoggerUtil;
import com.bofa.server.util.TaskManager;
import com.bofa.session.Session;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.handler
 * @date 2019/4/8
 */
@ChannelHandler.Sharable
public class RegisterRequestHandler extends SimpleChannelInboundHandler<RegisterRequestPacket> {
    public static final RegisterRequestHandler INSTANCE = new RegisterRequestHandler();
    static final Logger logger = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegisterRequestPacket requestPacket) throws Exception {
        String userName = requestPacket.getUserName();
        TaskManager.topicExecute("[" + userName + "]", "register", () -> {
            RegisterResponsePacket response = UserSv.register(requestPacket);
            User user = response.getUser();
            List<UserFriend> userFriends = response.getUserFriends();
            if (response.isSuccess()) {
                LoggerUtil.debug(logger, userName, "register success");
                SessionUtil.bindSession(new Session(user, userFriends), ctx.channel());
            } else {
                LoggerUtil.debug(logger, userName, "register fail");
            }
            return response;
        }, response -> ctx.channel().writeAndFlush(response), ctx.channel(), true);
    }
}
