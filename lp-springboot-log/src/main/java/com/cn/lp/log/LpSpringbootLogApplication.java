package com.cn.lp.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LpSpringbootLogApplication {

    private static final Logger LOG = LoggerFactory.getLogger(LpSpringbootLogApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LpSpringbootLogApplication.class, args);
        LOG.info("123");
    }

}
