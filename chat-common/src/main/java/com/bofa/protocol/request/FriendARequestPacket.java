package com.bofa.protocol.request;

import com.bofa.protocol.command.Command;
import lombok.Data;

import java.util.Date;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.protocol.request
 * @date 2019/4/27
 */
@Data
public class FriendARequestPacket extends AbstractRequestPacket{

    Integer approvaluserid;

    String approvalusername;

    String approvaldatetime;

    String approvalcontent;

    Integer approvaltouserid;

    String approvaltousername;

    Integer status;

    String approvalfailreason;

    @Override
    public Byte getCommand() {
        return Command.FRIENDA_REQUEST.command;
    }
}
