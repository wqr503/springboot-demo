package com.cn.lp.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by qirong on 2019/4/15.
 */
@Configuration
public class RedissonConfig {

    @Value("${redisson.json}")
    private String configJson;

    public String getConfigJson() {
        return configJson;
    }

}
