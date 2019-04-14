package com.bofa.protocol;

import com.alibaba.fastjson.parser.deserializer.OptionalCodec;
import com.bofa.protocol.command.Command;
import com.bofa.protocol.request.*;
import com.bofa.protocol.response.*;
import com.bofa.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol
 * @date 2019/4/2
 */
public class PacketCodeC {

    public static final int MAGIC_NUMBER = 0x12345678;
    public static final PacketCodeC INSTANCE = new PacketCodeC();

    private final Map<Byte, Class<? extends Packet>> packetTypeMap;
    private final Map<Byte, Serializer> serializerMap;

    public PacketCodeC() {
        packetTypeMap = new HashMap<>();
        serializerMap = new HashMap<>();
        serializerMap.put(Serializer.DEFAULT.getSerializerAlgorithm(), Serializer.DEFAULT);

        packetTypeMap.put(Command.LOGIN_REQUEST.command, LoginRequestPacket.class);
        packetTypeMap.put(Command.LOGIN_RESPONSE.command, LoginResponsePacket.class);
        packetTypeMap.put(Command.REGISTER_REQUEST.command, RegisterRequestPacket.class);
        packetTypeMap.put(Command.REGISTER_RESPONSE.command, RegisterResponsePacket.class);
        packetTypeMap.put(Command.LOGOUT_REQUEST.command, LogoutRequestPacket.class);
        packetTypeMap.put(Command.LOGOUT_RESPONSE.command, LogoutResponsePacket.class);
        packetTypeMap.put(Command.MESSAGE_REQUEST.command, MessageRequestPacket.class);
        packetTypeMap.put(Command.MESSAGE_RESPONSE.command, MessageResponsePacket.class);
        packetTypeMap.put(Command.MESSAGE_CALLBACK_REQUEST.command, MessageCallBackRequestPacket.class);
        packetTypeMap.put(Command.MESSAGE_CALLBACK_RESPONSE.command, MessageCallBackResponsePacket.class);
        packetTypeMap.put(Command.CHANGE_STATUS_REQUEST.command, ChangeStatusRequestPacket.class);
        packetTypeMap.put(Command.CHANGE_STATUS_RESPONSE.command, ChangeStatusResponsePacket.class);
    }

    public ByteBuf encode(ByteBuf byteBuf, Packet packet) {
        // 1. 序列化 Java 对象
        byte[] bytes = Serializer.DEFAULT.serialize(packet);

        // 2. 实际编码过程
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(Serializer.DEFAULT.getSerializerAlgorithm());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        System.out.println(packet);

        return byteBuf;
    }

    public Packet decode(ByteBuf byteBuf) {
        // 1. skip magic number, int类型占4个字节
        byteBuf.skipBytes(4);
        // 2. skip protocol
        byteBuf.skipBytes(1);
        // 3. get serializeAlgorithm
        byte serializeAlgorithm = byteBuf.readByte();
        // 4. get command
        byte command = byteBuf.readByte();
        // 5. get bytes length
        int length = byteBuf.readInt();
        // 6. get meta bytes
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        Serializer serializer = getSerializer(serializeAlgorithm);
        Class<? extends Packet> requestType = getRequestType(command);
        if (requestType != null && serializer != null) {
            System.out.println(requestType);
            return serializer.deserialize(requestType, bytes);
        }
        return null;
    }

    private Serializer getSerializer(byte serializeAlgorithm) {
        return serializerMap.get(serializeAlgorithm);
    }

    private Class<? extends Packet> getRequestType(byte command) {

        return packetTypeMap.get(command);
    }

}
