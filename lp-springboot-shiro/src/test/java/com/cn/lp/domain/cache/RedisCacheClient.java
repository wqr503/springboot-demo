package com.cn.lp.domain.cache;

import com.cn.lp.shiro.context.ShiroCacheClient;
import org.apache.shiro.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by qirong on 2019/4/28.
 */
public class RedisCacheClient implements ShiroCacheClient {

    private RedisTemplate<String, Object> redisTemplate;

    public RedisCacheClient(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public <O> O get(String key) {
        return (O) redisTemplate.opsForValue().get(key);
    }

    @Override
    public <O> O set(String key, O value) {
        redisTemplate.opsForValue().set(key, value);
        return value;
    }

    @Override
    public <O> O set(String key, O value, long expireTime) {
        redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.MILLISECONDS);
        return value;
    }

    @Override
    public boolean del(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public int del(Collection<String> keys) {
        Long num = redisTemplate.delete(keys);
        if (num == null) {
            return 0;
        }
        return num.intValue();
    }

    @Override
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    @Override
    public long dbSize() {
        return redisTemplate.execute((RedisCallback<Long>) connection -> connection.dbSize());
    }

    @Override
    public void flushDb() {
        redisTemplate.execute((RedisCallback) connection -> connection.dbSize());
    }
}
