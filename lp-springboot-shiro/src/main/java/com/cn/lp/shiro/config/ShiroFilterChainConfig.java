package com.cn.lp.shiro.config;


import java.util.Map;

/**
 * shiro过滤网址网址配置
 *
 * @author qirong
 * @date 2019/2/27
 */
public interface ShiroFilterChainConfig {

    /**
     * 过滤链
     * @return
     */
    Map<String, String> getFilterChainsMap();

}
