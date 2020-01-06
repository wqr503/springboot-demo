package com.cn.lp.shardingsphere.domain;

import org.apache.shardingsphere.api.sharding.ShardingAlgorithm;

/**
 * 表信息持有者
 * Created by qirong on 2019/5/21.
 */
public class TableHolder {

    public String tableName;

    public Class<? extends ShardingAlgorithm> databaseShardingAlgorithm;

    public Class<? extends ShardingAlgorithm> tableShardingAlgorithm;

    public String shardingColumn;

    public int shareNum;

    public String getShardingColumn() {
        return shardingColumn;
    }

    public TableHolder setShardingColumn(String shardingColumn) {
        this.shardingColumn = shardingColumn;
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    protected TableHolder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Class<? extends ShardingAlgorithm> getDatabaseShardingAlgorithm() {
        return databaseShardingAlgorithm;
    }

    protected TableHolder setDatabaseShardingAlgorithm(Class<? extends ShardingAlgorithm> databaseShardingAlgorithm) {
        this.databaseShardingAlgorithm = databaseShardingAlgorithm;
        return this;
    }

    public Class<? extends ShardingAlgorithm> getTableShardingAlgorithm() {
        return tableShardingAlgorithm;
    }

    protected TableHolder setTableShardingAlgorithm(Class<? extends ShardingAlgorithm> tableShardingAlgorithm) {
        this.tableShardingAlgorithm = tableShardingAlgorithm;
        return this;
    }

    public int getShareNum() {
        return shareNum;
    }

    protected TableHolder setShareNum(int shareNum) {
        this.shareNum = shareNum;
        return this;
    }

}
