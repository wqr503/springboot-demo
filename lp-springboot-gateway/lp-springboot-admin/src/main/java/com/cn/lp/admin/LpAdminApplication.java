package com.cn.lp.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@EnableAdminServer
@EnableEurekaClient
@SpringBootApplication
public class LpAdminApplication {

    private static Logger logger = LoggerFactory.getLogger(LpAdminApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LpAdminApplication.class, args);
        logger.info("------------------------------------");
        logger.info("------------------------------------");
        logger.info("------------------------------------");
        logger.info("------------------------------------");
        logger.info("------------------------------------");
    }

}
