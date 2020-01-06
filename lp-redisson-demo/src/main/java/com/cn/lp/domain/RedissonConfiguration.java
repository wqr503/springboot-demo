package com.cn.lp.domain;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Created by qirong on 2019/4/29.
 */
@Configuration
public class RedissonConfiguration {

    @Autowired
    private RedissonConfig redissonConfig;

    /**
     * 默认 Rediss 配置
     *
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient baseConfig2RedissonClient() {
        Config config = new Config();
        config.setCodec(new org.redisson.codec.KryoCodec());
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        return Redisson.create(config);
    }

    /**
     * Rediss 配置
     *
     * @return
     */
//    @Bean(destroyMethod = "shutdown")
//    public RedissonClient config2RedissonClient() throws IOException {
//        Config config = Config.fromJSON(redissonConfig.getConfigJson());
//        return Redisson.create(config);
//    }

}
