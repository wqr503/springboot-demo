package com.cn.lp.shardingsphere.domain;


import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * 默认分表规则
 * Created by xiang.wei on 2019/5/6
 *
 * @author xiang.wei
 */
@Service("preciseModuloTableShardingAlgorithm")
public class StandardTableShardingAlgorithm<T extends Comparable<?>> implements PreciseShardingAlgorithm<T> {

    /**
     * 不分表
     * @param collection           散表集合
     * @param preciseShardingValue 当前数据
     * @return
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<T> preciseShardingValue) {
        for (String table : collection) {
            return table;
        }
        return null;
    }

}
