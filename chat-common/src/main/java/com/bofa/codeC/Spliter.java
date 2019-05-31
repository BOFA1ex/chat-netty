package com.bofa.codeC;

import com.bofa.protocol.PacketCodeC;
import com.bofa.util.LocalDateTimeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.codeC
 * @date 2019/4/2
 */
public class Spliter extends LengthFieldBasedFrameDecoder {

    private static final int LENGTH_FIELD_OFFSET = 7;
    private static final int LENGTH_FIELD_LENGTH = 4;

    static final Logger logger = LoggerFactory.getLogger(Spliter.class);

    public Spliter() {
        super(Integer.MAX_VALUE, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (in.getInt(in.readerIndex()) != PacketCodeC.MAGIC_NUMBER) {
            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
            logger.debug("非法IP通讯拦截 原因[MAGIC_NUMBER 不一致] " + address.getHostString() + ":" + address.getPort());
            ctx.channel().close();
            return null;
        }
        return super.decode(ctx, in);
    }
}
