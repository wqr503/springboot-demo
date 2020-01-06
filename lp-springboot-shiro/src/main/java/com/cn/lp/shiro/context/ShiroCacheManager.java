package com.cn.lp.shiro.context;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * ShiroCacheManager CacheManager的实现
 * @author qirong
 * @date 2019/3/25
 */
public class ShiroCacheManager implements CacheManager {

    private static final Logger logger = LoggerFactory
        .getLogger(ShiroCacheManager.class);

    // fast lookup by name map
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>();

    private ShiroCacheClient cacheClient;

    /**
     * The Redis key prefix for caches
     */
    private String keyPrefix = "shiro_redis_cache:";

    /**
     * Returns the Redis session keys
     * prefix.
     *
     * @return The prefix
     */
    public String getKeyPrefix() {
        return keyPrefix;
    }

    /**
     * Sets the Redis sessions key
     * prefix.
     *
     * @param keyPrefix The prefix
     */
    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        logger.debug("获取名称为: " + name + " 的RedisCache实例");
        Cache c = caches.get(name);
        if (c == null) {
            // create a new cache instance
            c = new ShiroCache(cacheClient, keyPrefix);
            // add it to the cache collection
            caches.put(name, c);
        }
        return c;
    }

    public ShiroCacheManager setCacheClient(ShiroCacheClient cacheClient) {
        this.cacheClient = cacheClient;
        return this;
    }
}
