package com.cn.lp.shardingsphere.domain;

import com.cn.lp.shardingsphere.DatabaseShardingStrategy;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;

/**
 * 默认分库策略
 * Created by qirong on 2019/5/21.
 */
public class StandardDatabaseShardingStrategy implements DatabaseShardingStrategy {

    private PreciseShardingAlgorithm tableShardingAlgorithm;

    public StandardDatabaseShardingStrategy(PreciseShardingAlgorithm tableShardingAlgorithm) {
        this.tableShardingAlgorithm = tableShardingAlgorithm;
    }

    @Override
    public PreciseShardingAlgorithm getPreciseShardingAlgorithm(TableHolder tableHolder) {
        return tableShardingAlgorithm;
    }

}
