package com.bofa.codeC;

import com.bofa.protocol.Packet;
import com.bofa.protocol.PacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.codeC
 * @date 2019/4/2
 */
public class PacketDecoder extends ByteToMessageDecoder {

    public static final PacketDecoder INSTANCE = new PacketDecoder();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List out) throws Exception {
        out.add(PacketCodeC.INSTANCE.decode(byteBuf));
    }
}
