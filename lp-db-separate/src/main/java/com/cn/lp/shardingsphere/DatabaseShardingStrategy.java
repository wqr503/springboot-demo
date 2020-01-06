package com.cn.lp.shardingsphere;

import com.cn.lp.shardingsphere.domain.TableHolder;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;

/**
 * 分库策略，根据表返回对应的分库规则
 * Created by qirong on 2019/5/21.
 */
public interface DatabaseShardingStrategy {

    PreciseShardingAlgorithm getPreciseShardingAlgorithm(TableHolder tableHolder);

}
