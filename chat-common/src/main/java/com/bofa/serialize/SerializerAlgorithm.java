package com.bofa.serialize;

import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.serialize
 * @date 2019/4/2
 */
public enum SerializerAlgorithm {

    /**
     *
     */
    JSON((byte) 1),
    JDK((byte) 2);

    SerializerAlgorithm(byte serializerAlgorithm) {
        this.serializerAlgorithm = serializerAlgorithm;
    }

    byte serializerAlgorithm;

    public byte getSerializerAlgorithm() {
        return serializerAlgorithm;
    }

}
