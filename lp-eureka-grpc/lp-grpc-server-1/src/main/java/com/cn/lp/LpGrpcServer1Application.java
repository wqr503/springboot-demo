package com.cn.lp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LpGrpcServer1Application {

    public static void main(String[] args) {
        SpringApplication.run(LpGrpcServer1Application.class, args);
    }

}
