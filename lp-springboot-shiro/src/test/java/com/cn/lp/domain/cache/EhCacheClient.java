package com.cn.lp.domain.cache;


import com.cn.lp.shiro.context.ShiroCacheClient;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by qirong on 2019/3/27.
 */
public class EhCacheClient implements ShiroCacheClient {

    private Ehcache cache;

    public EhCacheClient(EhCacheCacheManager ehCacheCacheManager) {
        cache = Objects.requireNonNull(ehCacheCacheManager.getCacheManager()).getEhcache("shiroCache");
    }

    @Override
    public <O> O get(String key) {
        Element element = cache.get(key);
        if (element != null) {
            return (O) element.getObjectValue();
        }
        return null;
    }

    @Override
    public <O> O set(String key, O value) {
        cache.put(new Element(key, value));
        return value;
    }

    @Override
    public <O> O set(String key, O value, long expireTime) {
        cache.put(new Element(key, value, (int) (expireTime / 1000), 0));
        return value;
    }

    @Override
    public boolean del(String key) {
        return cache.remove(key);
    }

    @Override
    public int del(Collection<String> keys) {
        cache.removeAll(keys);
        return keys.size();
    }

    @Override
    public Set<String> keys(String pattern) {
        Set<String> dataSet = new HashSet<>();
        for (Object key : cache.getKeys()) {
            if (Pattern.matches(pattern, key.toString())) {
                dataSet.add(key.toString());
            }
        }
        return dataSet;
    }

    @Override
    public long dbSize() {
        return cache.getKeys().size();
    }

    @Override
    public void flushDb() {
        cache.removeAll();
    }
}
