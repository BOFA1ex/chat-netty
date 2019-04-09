package com.bofa.serialize;


import com.bofa.serialize.impl.JSONSerializer;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.serialize
 * @date 2019/4/2
 */
public interface Serializer {

    Serializer DEFAULT = new JSONSerializer();

    byte getSerializerAlgorithm();

    byte[] serialize(Object object);

    <T> T deserialize(Class<T> classType, byte[] bytes);
}
