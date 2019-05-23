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

    protected static <T,
            R extends AbstractResponsePacket>
    R post(String url, T request, Class<R> clazz) {
        return HttpUtil.postJsonData(url, request, clazz);
    }

    protected static <
            R extends AbstractResponsePacket>
    R get(String url, Class<R> clazz, String... params) {
        return HttpUtil.getData(url, clazz, params);
    }
}
