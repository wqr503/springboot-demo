package com.cn.lp.shiro.context;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 抽取Shiro缓存接口
 * @author qirong
 */
public class ShiroCache<K, V> implements Cache<K, V> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * The wrapped Jedis instance.
     */
    private ShiroCacheClient cacheClient;

    /**
     * The Redis key prefix for the sessions
     */
    private String keyPrefix = "shiro_redis_session:";

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

    /**
     * 通过一个JedisManager实例构造RedisCache
     */
    public ShiroCache(ShiroCacheClient cacheClient) {
        if (cacheClient == null) {
            throw new IllegalArgumentException("cacheClient argument cannot be null.");
        }
        this.cacheClient = cacheClient;
    }

    /**
     * Constructs a cache instance with the specified
     * Redis manager and using a custom key prefix.
     *
     * @param cacheClient  The cache manager instance
     * @param prefix The Redis key prefix
     */
    public ShiroCache(ShiroCacheClient cacheClient,
        String prefix) {

        this(cacheClient);

        // set the prefix
        this.keyPrefix = prefix;
    }

    /**
     * 获得byte[]型的key
     *
     * @param key
     * @return
     */
    private String getKey(K key) {
        if (key instanceof String) {
            return this.keyPrefix + key;
        } else {
            return key.toString();
        }
    }

    @Override
    public V get(K key) throws CacheException {
        logger.debug("根据key从Redis中获取对象 key [" + key + "]");
        try {
            if (key == null) {
                return null;
            } else {
                return cacheClient.get(getKey(key));
            }
        } catch (Throwable t) {
            throw new CacheException(t);
        }

    }

    @Override
    public V put(K key, V value) throws CacheException {
        logger.debug("根据key从存储 key [" + key + "]");
        try {
            cacheClient.set(getKey(key), value);
            return value;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        logger.debug("从redis中删除 key [" + key + "]");
        try {
            V previous = get(key);
            cacheClient.del(getKey(key));
            return previous;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public void clear() throws CacheException {
        cacheClient.flushDb();
    }

    @Override
    public int size() {
        return (int) cacheClient.dbSize();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keys() {
        try {
            Set<String> keys = cacheClient.keys(this.keyPrefix + "*");
            if (CollectionUtils.isEmpty(keys)) {
                return Collections.emptySet();
            } else {
                Set<K> newKeys = new HashSet<>();
                for (String key : keys) {
                    newKeys.add((K) key);
                }
                return newKeys;
            }
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    @Override
    public Collection<V> values() {
        try {
            Set<String> keys = cacheClient.keys(this.keyPrefix + "*");
            if (!CollectionUtils.isEmpty(keys)) {
                List<V> values = new ArrayList<V>(keys.size());
                for (String key : keys) {
                    V value = get((K) key);
                    if (value != null) {
                        values.add(value);
                    }
                }
                return Collections.unmodifiableList(values);
            } else {
                return Collections.emptyList();
            }
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

}
