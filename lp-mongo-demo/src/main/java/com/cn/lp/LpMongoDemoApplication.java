package com.cn.lp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * 要加入EnableMongoAuditing这个才会自动插入属性
 */
@EnableMongoAuditing
@SpringBootApplication
public class LpMongoDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LpMongoDemoApplication.class, args);
    }

}
