package com.bofa.attribute;

import com.bofa.session.Session;
import io.netty.util.AttributeKey;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.attribute
 * @date 2019/4/2
 */
public interface Attributes {
    AttributeKey<Session> SESSION = AttributeKey.newInstance("session");
}
