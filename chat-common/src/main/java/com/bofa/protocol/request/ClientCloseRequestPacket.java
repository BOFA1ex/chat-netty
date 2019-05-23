package com.bofa.protocol.request;

import com.bofa.protocol.command.Command;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.protocol.request
 * @date 2019/5/23
 */
public class ClientCloseRequestPacket extends AbstractRequestPacket{
    @Override
    public Byte getCommand() {
        return Command.CLIENT_CLOSE.command;
    }
}
