package com.cn.lp.shardingsphere.domain;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author jay.xiang
 * @create 2019/4/29 19:56
 */
@Configuration
public class DataSourceConfig {

    /**
     * 扫包路径
     */
    @Value("${spring.shardingsphere.sharding.scan.basePack}")
    private String scanBasePack;

    /**
     * 是否打印SQL
     */
    @Value("${spring.shardingsphere.sharding.showSQL}")
    private boolean showSQL;

    /**
     * 默认散列个数
     */
    @Value("${spring.shardingsphere.sharding.defaultShareNum : 1}")
    private int defaultShareNum;

    public String getScanBasePack() {
        return this.scanBasePack;
    }

    public boolean isShowSQL() {
        return this.showSQL;
    }

    public int getDefaultShareNum() {
        return this.defaultShareNum;
    }
}
