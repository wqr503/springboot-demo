package com.cn.lp.domain;

import com.cn.lp.config.ShiroFilterChainConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * shiro过滤网址网址配置
 * Created by qirong on 2019/2/27.
 */
@Component(value = "ShiroFilterChainConfig")
@ConfigurationProperties(prefix = "shiro")
public class TestShiroFilterChainConfig implements ShiroFilterChainConfig {

    /**
     * 过滤列表
     */
    private List<String> filterChains = new ArrayList<>();

    public TestShiroFilterChainConfig() {

    }

    public List<String> getFilterChains() {
        return filterChains;
    }

    @Override
    public Map<String, String> getFilterChainsMap() {
        LinkedHashMap<String, String> dataMap = new LinkedHashMap<>();
        for (int index = 0; index + 2 < filterChains.size(); index = index + 2) {
            dataMap.put(filterChains.get(index), filterChains.get(index + 1));
        }
        return dataMap;
    }
}
