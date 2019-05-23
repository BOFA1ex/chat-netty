package com.bofa;


import com.alibaba.druid.pool.DruidDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa
 * @date 2019/4/17
 */

public class H2Test {

    private static Properties properties;

    private static String jdbcUrl;

    private static String userName;

    private static String password;

    public static void main(String[] args) {
    }

    static {
        properties = System.getProperties();
        InputStream in = null;
        try {
            in = H2Test.class.getResourceAsStream("/application.properties");
            properties.load(in);
            jdbcUrl = properties.getProperty("jdbc.driver");
            userName = properties.getProperty("jdbc.username");
            password = properties.getProperty("jdbc.password");
        } catch (IOException e) {
        }finally {
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
