package com.cn.lp.shiro.context;

import java.util.Collection;
import java.util.Set;

/**
 * 统一ShiroCache客户端接口
 * @author qirong
 * @date 2019/3/27
 */
public interface ShiroCacheClient {

    /**
     * 获取值
     * @param key
     * @param <O>
     * @return
     */
    <O> O get(String key);

    /**
     * 设置值
     * @param key
     * @param value
     * @param <O>
     * @return
     */
    <O> O set(String key, O value);

    /**
     * 设置值，有效期
     * @param key
     * @param value
     * @param expireTime 毫秒
     * @return
     */
    <O> O set(String key, O value, long expireTime);

    /**
     * 删除值
     * @param key
     * @return
     */
    boolean del(String key);

    /**
     * 删除多个值
     * @param keys
     * @return
     */
    int del(Collection<String> keys);

    /**
     * 获取Key列表
     * @param pattern
     * @return
     */
    Set<String> keys(String pattern);

    /**
     * 获取数据库大小
     * @return
     */
    long dbSize();

    /**
     * 清空数据库
     */
    void flushDb();

}
