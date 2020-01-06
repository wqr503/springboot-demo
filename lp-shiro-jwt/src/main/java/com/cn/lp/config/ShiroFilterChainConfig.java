package com.cn.lp.config;


import java.util.Map;

/**
 * shiro过滤网址网址配置
 *
 * @author qirong
 * @date 2019/2/27
 */
public interface ShiroFilterChainConfig {

    Map<String, String> getFilterChainsMap();

}
