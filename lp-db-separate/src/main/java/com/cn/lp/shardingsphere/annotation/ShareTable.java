package com.cn.lp.shardingsphere.annotation;

import com.cn.lp.shardingsphere.domain.StandardDatabaseShardingAlgorithm;
import com.cn.lp.shardingsphere.domain.StandardTableShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.ShardingAlgorithm;

import java.lang.annotation.*;

/**
 * 散列表
 *
 * @author KGTny
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ShareTable {

    /**
     * 分表规则
     * @return
     */
    Class<? extends ShardingAlgorithm> tableShardingAlgorithm() default StandardTableShardingAlgorithm.class;

    /**
     * 分库规则
     * @return
     */
    Class<? extends ShardingAlgorithm> databaseShardingAlgorithm() default StandardDatabaseShardingAlgorithm.class;

    /**
     * 散列个数
     * @return
     */
    int shareNum() default 1;

}
