package com.cn.lp.shardingsphere.domain;


import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * 数据库分片策略
 *
 * @author jay.xiang
 * @create 2019/4/29 19:56
 */
@Service("preciseModuloDatabaseShardingAlgorithm")
public class StandardDatabaseShardingAlgorithm<T extends Comparable<?>> implements PreciseShardingAlgorithm<T> {

    /**
     * 不分库
     * @param collection           散表集合
     * @param preciseShardingValue 当前数据
     * @return
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<T> preciseShardingValue) {
        for (String db : collection) {
            return db;
        }
        return null;
    }

}
