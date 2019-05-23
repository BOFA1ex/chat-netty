package com.bofa.protocol.request;

import com.bofa.protocol.command.Command;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.protocol.request
 * @date 2019/5/5
 */
@Data
public class FriendCRequestPacket extends AbstractRequestPacket{

    private Integer userid;

    private Integer userfriendid;

    private String userfriendName;

    private String operatetime;

    private Integer status;

    private Date lastchattime;

    @Override
    public Byte getCommand() {
        return Command.FRIENDC_REQUEST.command;
    }
}
