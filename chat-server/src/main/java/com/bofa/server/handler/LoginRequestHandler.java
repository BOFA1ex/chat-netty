package com.bofa.server.handler;

import com.bofa.attribute.NoticeType;
import com.bofa.attribute.UserStatus;
import com.bofa.entity.User;
import com.bofa.entity.UserNotice;
import com.bofa.protocol.request.LoginRequestPacket;
import com.bofa.protocol.request.LogoutRequestPacket;
import com.bofa.protocol.response.AbstractResponsePacket;
import com.bofa.protocol.response.LoginResponsePacket;
import com.bofa.protocol.response.NoticeResponsePacket;
import com.bofa.server.service.UserSv;
import com.bofa.server.util.LoggerUtil;
import com.bofa.server.util.TaskManager;
import com.bofa.session.Session;
import com.bofa.util.LocalDateTimeUtil;
import com.bofa.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.handler
 * @date 2019/4/3
 */
@ChannelHandler.Sharable
public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {

    static final Logger logger = LoggerFactory.getLogger(LoginRequestHandler.class);
    public static final LoginRequestHandler INSTANCE = new LoginRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestPacket loginRequestPacket) throws Exception {
        String userName = loginRequestPacket.getUserName();
        TaskManager.topicExecute("[" + userName + "]", "login", () -> {
            User user;
            Integer prevUserId;
            /**
             * 获取上一次登录的session信息, 将异地通知发给上一个设备
             */
            if ((user = SessionUtil.getUser(userName)) != null) {
                prevUserId = user.getUserId();
                Channel preChannel = SessionUtil.getChannel(prevUserId);
                TaskManager.execute("通知上一个登录设备", () -> {
                    UserNotice notice = mapper(ctx.channel(), user.getUserName(), prevUserId);
                    preChannel.writeAndFlush(new NoticeResponsePacket(notice)).addListener(future -> {
                        Optional.ofNullable(future.cause()).ifPresent(Throwable::printStackTrace);
                        if (future.isSuccess()) {
                            logger.debug("通知上一个登录设备成功");
                        }
                    });
                });
            }
            LoginResponsePacket response = UserSv.login(loginRequestPacket);
            if (response.isSuccess()) {
                LoggerUtil.info(logger, userName, "login success");
                SessionUtil.bindSession(new Session(response.getUser(), response.getUserFriends(), response.getUserNotices()), ctx.channel());
            } else {
                LoggerUtil.error(logger, userName, "login fail");
            }
            return response;
        }, (response) -> ctx.channel().writeAndFlush(response), ctx.channel(), true);
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
        if (SessionUtil.hasLogin(ctx.channel())) {
            User user = SessionUtil.getSession(ctx.channel()).getUser();
            String commonIp = ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
            TaskManager.execute(user.getUserName() + " force logout",
                    forceLogout(user.getUserId(), commonIp));
            SessionUtil.unbindSession(ctx.channel());
        }
        super.channelInactive(ctx);
    }

    /**
     * 客户端Channel强制注销
     *
     * @param userId
     * @param commonIp
     * @return
     */
    private Runnable forceLogout(Integer userId, String commonIp) {
        return () -> {
            LogoutRequestPacket requestPacket = new LogoutRequestPacket();
            requestPacket.setUserId(userId);
            requestPacket.setStatus(UserStatus.OFFLINE.status);
            requestPacket.setCommonIp(commonIp);
            UserSv.logout(requestPacket);
        };
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = ((NioSocketChannel) ctx.channel()).remoteAddress();
        LoggerUtil.debug(logger, "NEW CLIENT LINKED", address.getHostString() + ":" + address.getPort());
        super.channelActive(ctx);
    }

    private UserNotice mapper(Channel channel, String userName, Integer userId) {
        UserNotice notice = new UserNotice();
        String hostString = ((InetSocketAddress) channel.remoteAddress()).getHostString();
        notice.setNoticedatetime(LocalDateTimeUtil.now0());
        notice.setNoticetype(NoticeType.OTHER_PLACE_LOGIN.type);
        notice.setNoticecontent("你的账号在另一个设备 [" + hostString + "] 登录");
        notice.setUsername("SYSTEM");
        notice.setNoticename(userName);
        notice.setNoticeid(userId);
        return notice;
    }
}
