package com.cn.lp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching  //开启缓存
@EnableScheduling
public class LpTestServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LpTestServiceApplication.class, args);
    }

}
