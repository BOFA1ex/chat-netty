package com.bofa.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.config
 * @date 2019/5/17
 */
@Component
public class PropertiesValueConfig {

    @Value("${chat.server.host}")
    private String host;

    @Value("${chat.server.port}")
    private int port;

    String getHost() {
        return host;
    }

    int getPort() {
        return port;
    }
}
