package com.bofa.server.handler;

import com.bofa.attribute.UserStatus;
import com.bofa.entity.User;
import com.bofa.protocol.command.Command;
import com.bofa.protocol.request.AbstractRequestPacket;
import com.bofa.protocol.request.LogoutRequestPacket;
import com.bofa.server.TaskManager;
import com.bofa.server.service.UserSv;
import com.bofa.server.util.LoggerUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.handler
 * @date 2019/4/8
 */
@ChannelHandler.Sharable
public class ChatServerHandler extends SimpleChannelInboundHandler<AbstractRequestPacket> {

    public static final ChatServerHandler INSTANCE = new ChatServerHandler();

    static final Logger logger = LoggerFactory.getLogger(ChatServerHandler.class);

    private static final Map<Byte, SimpleChannelInboundHandler<? extends AbstractRequestPacket>> handleMap;

    static {
        handleMap = new HashMap<>();
        handleMap.put(Command.LOGIN_REQUEST.command, LoginRequestHandler.INSTANCE);
        handleMap.put(Command.REGISTER_REQUEST.command, RegisterRequestHandler.INSTANCE);
        handleMap.put(Command.LOGOUT_REQUEST.command, LogoutRequestHandler.INSTANCE);
        handleMap.put(Command.MESSAGE_REQUEST.command, MessageRequestHandler.INSTANCE);
        handleMap.put(Command.MESSAGE_CALLBACK_REQUEST.command, MessageCallBackRequestHandler.INSTANCE);
        handleMap.put(Command.CHANGE_STATUS_REQUEST.command, ChangeStatusRequestHandler.INSTANCE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractRequestPacket packet) throws Exception {
        handleMap.get(packet.getCommand()).channelRead(ctx, packet);
    }

    /**
     * 客户端socket长连接断开，强制注销更改登录状态以及取消绑定session
     * 考虑IO阻塞，这里的forceLogout 交给业务线程池处理
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        /**
         * 如果未正常注销
         */
        if (SessionUtil.hasLogin(ctx.channel())){
            User user = SessionUtil.getSession(ctx.channel()).getUser();
            LoggerUtil.debug(logger, user.getUserName(), "force logout");
            TaskManager.execute(SessionUtil.getSession(ctx.channel()) + "force logout", forceLogout(user.getUserId()));
            SessionUtil.unbindSession(ctx.channel());
        }
        super.channelInactive(ctx);
    }

    private Runnable forceLogout(Integer userId) {
        return () -> {
            LogoutRequestPacket requestPacket = new LogoutRequestPacket();
            requestPacket.setUserId(userId);
            requestPacket.setStatus(UserStatus.OFFLINE.status);
            UserSv.logout(requestPacket);
        };
    }
}
