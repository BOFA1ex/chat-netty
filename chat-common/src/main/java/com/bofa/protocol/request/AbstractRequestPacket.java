package com.bofa.protocol.request;

import com.bofa.protocol.Packet;
import lombok.Data;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol.request
 * @date 2019/4/5
 */
@Data
public abstract class AbstractRequestPacket extends Packet{

    Integer userid;
}
