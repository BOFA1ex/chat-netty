package com.bofa.client;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client
 * @date 2019/4/17
 */

@SpringBootApplication
public class ChatClientApplication {

    public static void main(String[] args) {

        SpringApplication application = new SpringApplication(ChatClientApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}
