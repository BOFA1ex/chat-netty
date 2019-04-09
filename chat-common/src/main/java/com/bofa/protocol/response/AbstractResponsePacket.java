package com.bofa.protocol.response;

import com.bofa.protocol.Packet;
import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol.response
 * @date 2019/4/5
 */
@Data
public abstract class AbstractResponsePacket extends Packet {

    boolean success;

    String message;

    String code;

}
