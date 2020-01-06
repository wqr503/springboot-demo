package com.cn.lp.shardingsphere.domain;

import com.cn.lp.reflect.ReflectUtils;
import com.cn.lp.scan.ClassScanner;
import com.cn.lp.shardingsphere.DatabaseShardingStrategy;
import com.cn.lp.shardingsphere.TableShardingStrategy;
import com.cn.lp.shardingsphere.annotation.ShardingColumn;
import com.cn.lp.shardingsphere.annotation.ShareTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.sharding.ShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.springframework.util.Assert;

import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 表加载器
 * Created by qirong on 2019/5/21.
 */
public class TableLoader extends ClassScanner {

    private List<TableHolder> tableList = new ArrayList<>();

    private int shareNum;

    public TableLoader setDefaultShareNum(int shareNum) {
        this.shareNum = shareNum;
        return this;
    }

    /**
     * 根据路径初始化加载器
     * @param packs
     */
    public void init(String... packs) {
        for (Class clazz : this.getClasses(packs)) {
            Table table = (Table) clazz.getAnnotation(Table.class);
            Assert.notNull(table, clazz + "没有Table注解");
            TableHolder holder = new TableHolder();
            holder.setTableName(table.name());
            holder.setShardingColumn(gainShareKey(clazz));
            ShareTable shareTable = (ShareTable) clazz.getAnnotation(ShareTable.class);
            Class<? extends ShardingAlgorithm> databaseShardingAlgorithm = StandardDatabaseShardingAlgorithm.class;
            Class<? extends ShardingAlgorithm> tableShardingAlgorithm = StandardTableShardingAlgorithm.class;
            int shareNum = this.shareNum;
            if(shareTable != null) {
                databaseShardingAlgorithm = shareTable.databaseShardingAlgorithm();
                tableShardingAlgorithm = shareTable.tableShardingAlgorithm();
                shareNum = shareTable.shareNum();
            }
            holder.setShareNum(shareNum);
            holder.setDatabaseShardingAlgorithm(databaseShardingAlgorithm);
            holder.setTableShardingAlgorithm(tableShardingAlgorithm);
            tableList.add(holder);
        }
    }

    private String gainShareKey(Class clazz) {
        List<Field> fieldList = ReflectUtils.getDeepFieldByAnnotation(clazz, ShardingColumn.class);
        if(!fieldList.isEmpty()) {
            Field field = fieldList.get(0);
            String shareKey = field.getAnnotation(ShardingColumn.class).name();
            if(StringUtils.isBlank(shareKey)) {
                shareKey = field.getName();
            }
            return shareKey;
        } else {
            fieldList = ReflectUtils.getDeepFieldByAnnotation(clazz, Id.class);
            Assert.notEmpty(fieldList, clazz + "没有Id注解");
            return fieldList.get(0).getName();
        }
    }

    /**
     * 获取表列表
     * @return
     */
    public List<TableHolder> getTableList() {
        return Collections.unmodifiableList(tableList);
    }
}
