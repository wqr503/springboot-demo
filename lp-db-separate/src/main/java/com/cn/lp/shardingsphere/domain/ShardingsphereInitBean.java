package com.cn.lp.shardingsphere.domain;

import com.cn.lp.scan.AnnotationClassFilter;
import com.cn.lp.shardingsphere.DatabaseShardingStrategy;
import com.cn.lp.shardingsphere.TableShardingStrategy;
import com.google.common.collect.ImmutableMap;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.core.constant.properties.ShardingPropertiesConstant;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

import javax.persistence.Table;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by qirong on 2019/5/21.
 */
public abstract class ShardingsphereInitBean {

    private final static String DATA_SOURCE_NAME = "db";

    public TableLoader createTableLoader(DataSourceConfig config) {
        TableLoader tableLoader = new TableLoader();
        tableLoader.addFilter(AnnotationClassFilter.ofInclude(Table.class));
        tableLoader.setDefaultShareNum(config.getDefaultShareNum());
        tableLoader.init(config.getScanBasePack());
        return tableLoader;
    }

    /**
     * 构建数据源
     *
     * @return
     * @throws SQLException
     */
    public DataSource createShardingDataSource(DataSourceConfig config, TableLoader tableLoader,
        PreciseShardingAlgorithm tableShardingAlgorithm) throws SQLException {
        return this.createShardingDataSource(config, tableLoader, new StandardTableShardingStrategy(tableShardingAlgorithm), new StandardDatabaseShardingStrategy(new StandardDatabaseShardingAlgorithm()));
    }

    /**
     * 构建数据源
     *
     * @return
     * @throws SQLException
     */
    public DataSource createShardingDataSource(DataSourceConfig config, TableLoader tableLoader,
        TableShardingStrategy tableShardingStrategy) throws SQLException {
        return this.createShardingDataSource(config, tableLoader, tableShardingStrategy, new StandardDatabaseShardingStrategy(new StandardDatabaseShardingAlgorithm()));
    }

    /**
     * 构建数据源
     *
     * @return
     * @throws SQLException
     */
    public DataSource createShardingDataSource(DataSourceConfig config, TableLoader tableLoader,
        TableShardingStrategy tableShardingStrategy, DatabaseShardingStrategy databaseShardingStrategy) throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        for (TableHolder tableHolder : tableLoader.getTableList()) {
            shardingRuleConfig.getBindingTableGroups().add(tableHolder.getTableName());
            shardingRuleConfig.getTableRuleConfigs().add(this.createTableRuleConfiguration(DATA_SOURCE_NAME, tableHolder.getTableName(), tableHolder.getShareNum(),
                tableHolder.getShardingColumn(), databaseShardingStrategy.getPreciseShardingAlgorithm(tableHolder),
                tableShardingStrategy.getPreciseShardingAlgorithm(tableHolder)
            ));
        }
        shardingRuleConfig.setDefaultDataSourceName(DATA_SOURCE_NAME);
        Properties properties = new Properties();
        properties.setProperty(ShardingPropertiesConstant.SQL_SHOW.getKey(), config.isShowSQL() ? "true" : "false");
        return ShardingDataSourceFactory.createDataSource(ImmutableMap.of(DATA_SOURCE_NAME, this.createDataSource()), shardingRuleConfig, properties);
    }

    private TableRuleConfiguration createTableRuleConfiguration(String db, String table, int shareNum,
        String shardingColumn, PreciseShardingAlgorithm preciseModuloDatabaseShardingAlgorithm,
        PreciseShardingAlgorithm preciseModuloTableShardingAlgorithm) {
        StringBuilder ordersActualDataNodes = new StringBuilder();
        ordersActualDataNodes
            .append(db)
            .append(".")
            .append(table);
        if (shareNum > 0) {
            ordersActualDataNodes.append("_$->{0..");
            ordersActualDataNodes.append(shareNum);
            ordersActualDataNodes.append("}");
        } else {
            ordersActualDataNodes.append("_$->{0}");
        }
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration(table, ordersActualDataNodes.toString());
        orderTableRuleConfig.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration(shardingColumn, preciseModuloDatabaseShardingAlgorithm));
        orderTableRuleConfig.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration(shardingColumn, preciseModuloTableShardingAlgorithm));
        return orderTableRuleConfig;
    }

    public abstract DataSource createDataSource();

}
