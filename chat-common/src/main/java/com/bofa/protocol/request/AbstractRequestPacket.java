package com.bofa.protocol.request;

import com.bofa.protocol.Packet;
import com.bofa.protocol.command.Command;
import com.bofa.protocol.response.AbstractResponsePacket;
import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol.request
 * @date 2019/4/5
 */
@Data
public abstract class AbstractRequestPacket extends Packet{

    Integer userId;

    public static class DefaultAbstractRequestPacket extends AbstractRequestPacket {
        @Override
        public Byte getCommand() {
            return Command.DEFAULT_REQUEST.command;
        }
    }
}
