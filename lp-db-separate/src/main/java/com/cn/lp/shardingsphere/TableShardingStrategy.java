package com.cn.lp.shardingsphere;

import com.cn.lp.shardingsphere.domain.TableHolder;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;

/**
 * 分表策略，根据表返回对应的分表规则
 * Created by qirong on 2019/5/21.
 */
public interface TableShardingStrategy {

    PreciseShardingAlgorithm getPreciseShardingAlgorithm(TableHolder tableHolder);

}
