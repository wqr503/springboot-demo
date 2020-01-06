package com.cn.lp.test.configuration;


import com.alibaba.druid.pool.DruidDataSource;
import com.cn.lp.shardingsphere.domain.*;
import com.cn.lp.test.domain.OddEvenTableShardingAlgorithm;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.core.constant.properties.ShardingPropertiesConstant;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author jay.xiang
 * @create 2019/4/29 19:56
 */
@Configuration
public class DataSourceConfiguration extends ShardingsphereInitBean {

    @Value("${spring.shardingsphere.datasource.username}")
    private String username0;
    @Value("${spring.shardingsphere.datasource.url}")
    private String url0;
    @Value("${spring.shardingsphere.datasource.password}")
    private String password0;

    @Autowired
    private OddEvenTableShardingAlgorithm oddEvenTableShardingAlgorithm;

    @Autowired
    private DataSourceConfig dataSourceConfig;

    /**
     * 设置数据源
     *
     * @return
     * @throws SQLException
     */
    @Bean(name = "DataSource")
    DataSource getShardingDataSource() throws SQLException {
        return this.createShardingDataSource(dataSourceConfig, getTableLoader(), oddEvenTableShardingAlgorithm);
    }

    @Bean
    TableLoader getTableLoader() {
        TableLoader tableLoader = this.createTableLoader(dataSourceConfig);
        return tableLoader;
    }

    @Override
    public DataSource createDataSource() {
        DruidDataSource dataSource = null;
        dataSource = new DruidDataSource();
        dataSource.setUrl(url0);
        dataSource.setUsername(username0);
        dataSource.setPassword(password0);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return dataSource;
    }

}
