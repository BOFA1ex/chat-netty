package com.bofa.protocol.response;

import com.bofa.exception.ChatException;
import com.bofa.protocol.Packet;
import com.bofa.protocol.command.Command;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol.response
 * @date 2019/4/5
 */
@Data
public abstract class AbstractResponsePacket extends Packet {

    public static final String CODE_SC = "200";

    public static final String CODE_FA = "500";

    boolean success = false;

    String message;

    String code;

    public boolean isConnectionError() {
        if (StringUtils.isEmpty(code)){
            return false;
        }
        switch (code) {
            case CODE_SC:
                break;
            case CODE_FA:
                return true;
            default:
                break;
        }
        return false;
    }

    public static class DefaultAbstractResponsePacket extends AbstractResponsePacket {
        @Override
        public Byte getCommand() {
            return Command.DEFAULT_RESPONSE.command;
        }
    }
}
