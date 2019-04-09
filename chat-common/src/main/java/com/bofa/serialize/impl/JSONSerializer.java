package com.bofa.serialize.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bofa.serialize.Serializer;
import com.bofa.serialize.SerializerAlgorithm;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.serialize.impl
 * @date 2019/4/2
 */
public class JSONSerializer implements Serializer {

    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.JSON.getSerializerAlgorithm();
    }

    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSONObject.parseObject(bytes, clazz);
    }
}
