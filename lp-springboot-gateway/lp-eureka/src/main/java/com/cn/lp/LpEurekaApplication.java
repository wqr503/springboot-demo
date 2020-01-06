package com.cn.lp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class LpEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LpEurekaApplication.class, args);
    }

}
