package com.bofa.codeC;

import com.bofa.protocol.Packet;
import com.bofa.protocol.PacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.codeC
 * @date 2019/4/8
 */
@ChannelHandler.Sharable
public class PacketCodeHandler extends MessageToMessageCodec<ByteBuf, Packet> {

    public static final PacketCodeHandler INSTANCE = new PacketCodeHandler();

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, List<Object> list) throws Exception {
        System.out.println("encode packet: " + packet);
        ByteBuf byteBuf = ctx.channel().alloc().ioBuffer();
        ByteBuf encode = PacketCodeC.INSTANCE.encode(byteBuf, packet);
        list.add(encode);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        Packet decode = PacketCodeC.INSTANCE.decode(byteBuf);
        System.out.println("decode packet: " + decode);
        list.add(decode);
    }
}
