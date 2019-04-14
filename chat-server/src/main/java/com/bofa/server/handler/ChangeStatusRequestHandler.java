package com.bofa.server.handler;

import com.bofa.attribute.UserStatus;
import com.bofa.protocol.request.ChangeStatusRequestPacket;
import com.bofa.protocol.response.ChangeStatusResponsePacket;
import com.bofa.protocol.response.LogoutResponsePacket;
import com.bofa.server.TaskManager;
import com.bofa.server.service.UserSv;
import com.bofa.server.util.LoggerUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.handler
 * @date 2019/4/13
 */
public class ChangeStatusRequestHandler extends SimpleChannelInboundHandler<ChangeStatusRequestPacket> {

    static final ChangeStatusRequestHandler INSTANCE = new ChangeStatusRequestHandler();
    static final Logger logger = LoggerFactory.getLogger(ChangeStatusRequestHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChangeStatusRequestPacket requestPacket) throws Exception {
        TaskManager.execute("changeStatus", () -> {
            Integer status = requestPacket.getStatus();
            String userName = SessionUtil.getSession(ctx.channel()).getUser().getUserName();
            ChangeStatusResponsePacket response = UserSv.changeStatus(requestPacket);
            response.setStatus(status);
            if (response.isSuccess()) {
                if (status == UserStatus.OFFLINE.status) {
                    LoggerUtil.debug(logger, userName, "logout");
                    SessionUtil.unbindSession(ctx.channel());
                    LogoutResponsePacket logoutResponsePacket = new LogoutResponsePacket();
                    logoutResponsePacket.setSuccess(true);
                    logoutResponsePacket.setUserName(userName);
                    ctx.channel().writeAndFlush(logoutResponsePacket);
                    return;
                } else if (status == UserStatus.VISIBLE.status) {
                    LoggerUtil.debug(logger, userName, "changeStatus -> " + UserStatus.VISIBLE.comment);
                } else if (status == UserStatus.ONLINE.status) {
                    LoggerUtil.debug(logger, userName, "changeStatus -> " + UserStatus.ONLINE.comment);
                }
                ctx.channel().writeAndFlush(response);
            } else {
                LoggerUtil.error(logger, userName, response.getMessage());
            }
        });
    }
}
