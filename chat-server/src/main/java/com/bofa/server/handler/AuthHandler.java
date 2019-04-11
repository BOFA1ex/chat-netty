package com.bofa.server.handler;

import com.bofa.server.util.LoggerUtil;
import com.bofa.session.Session;
import com.bofa.util.SessionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.handler
 * @date 2019/4/3
 */
@ChannelHandler.Sharable
public class AuthHandler extends ChannelInboundHandlerAdapter {

    public static final AuthHandler INSTANCE = new AuthHandler();
    static final Logger logger = LoggerFactory.getLogger(AuthHandler.class);

    /**
     * SimpleChannelInboundHandler的acceptInboundMessage，this.matcher会缓存java对象的类型
     * 会根据请求的java对象来判断是否需要进行channelRead.
     * LoginRequestHandler 如果登录失败，需要对通道做释放操作，该逻辑尽可能抽离由其他handler处理。
     * 如果登录成功，则从pipeLine链移除该handler.
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!SessionUtil.hasLogin(ctx.channel())) {
            ctx.channel().close();
        } else {
            ctx.pipeline().remove(this);
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LoggerUtil.debug(logger, SessionUtil.getSession(ctx.channel()).getUser().getUserName(), "already login");
        super.handlerRemoved(ctx);
    }
}
