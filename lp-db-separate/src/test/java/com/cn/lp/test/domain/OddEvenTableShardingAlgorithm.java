package com.cn.lp.test.domain;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * 奇偶分表策略
 * Created by qirong on 2019/5/22.
 */
@Service("oddEvenTableShardingAlgorithm")
public class OddEvenTableShardingAlgorithm implements PreciseShardingAlgorithm<Long> {

    /**
     * 分表
     *
     * @param collection           散表集合
     * @param preciseShardingValue 当前数据
     * @return
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Long> preciseShardingValue) {
        int num = (int) (preciseShardingValue.getValue() % 2);
        int index = 0;
        for (String table : collection) {
            if (index == num) {
                return table;
            }
            index = index + 1;
        }
        return null;
    }

}
