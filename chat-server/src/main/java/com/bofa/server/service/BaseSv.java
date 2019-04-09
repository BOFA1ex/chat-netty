package com.bofa.server.service;

import com.bofa.exception.ChatErrorCode;
import com.bofa.exception.ChatException;
import com.bofa.protocol.request.AbstractRequestPacket;
import com.bofa.protocol.response.AbstractResponsePacket;
import com.bofa.server.util.HttpUtil;

import java.util.Arrays;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.server.service
 * @date 2019/4/8
 */
public abstract class BaseSv {

    protected static <T extends AbstractRequestPacket,
            R extends AbstractResponsePacket>
    R post(String url, T request, Class<R> clazz){
        R response = HttpUtil.postJsonData(url, request, clazz);
        response.setVersion(request.getVersion());
        return response;
    }
}
