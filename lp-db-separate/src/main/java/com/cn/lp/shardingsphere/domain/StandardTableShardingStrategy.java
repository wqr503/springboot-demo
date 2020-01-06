package com.cn.lp.shardingsphere.domain;

import com.cn.lp.shardingsphere.TableShardingStrategy;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;

/**
 * 分表策略
 * Created by qirong on 2019/5/21.
 */
public class StandardTableShardingStrategy implements TableShardingStrategy {

    private PreciseShardingAlgorithm tableShardingAlgorithm;

    public StandardTableShardingStrategy(PreciseShardingAlgorithm tableShardingAlgorithm) {
        this.tableShardingAlgorithm = tableShardingAlgorithm;
    }

    @Override
    public PreciseShardingAlgorithm getPreciseShardingAlgorithm(TableHolder tableHolder) {
        return tableShardingAlgorithm;
    }
}
