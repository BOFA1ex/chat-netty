package com.bofa.server.handler;

import com.bofa.protocol.request.LogoutRequestPacket;
import com.bofa.protocol.response.LogoutResponsePacket;
import com.bofa.server.service.UserSv;
import com.bofa.server.util.LoggerUtil;
import com.bofa.server.util.TaskManager;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.handler
 * @date 2019/4/9
 */
public class LogoutRequestHandler extends SimpleChannelInboundHandler<LogoutRequestPacket> {

    static final LogoutRequestHandler INSTANCE = new LogoutRequestHandler();
    static final Logger logger = LoggerFactory.getLogger(LogoutRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogoutRequestPacket requestPacket) throws Exception {
        TaskManager.execute("logout", () -> {
            LogoutResponsePacket response = UserSv.logout(requestPacket);
            String userName = SessionUtil.getSession(ctx.channel()).getUser().getUserName();
            response.setUserName(userName);
            if (response.isSuccess()) {
                LoggerUtil.debug(logger, userName, "logout");
                SessionUtil.unbindSession(ctx.channel());
            } else {
                LoggerUtil.error(logger, userName, response.getMessage());
            }
            ctx.channel().writeAndFlush(response);
        });
    }
}
