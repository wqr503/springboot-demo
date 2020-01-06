package com.cn.lp.domain;

import com.cn.lp.dynamic.RecordExt;
import com.cn.lp.dynamic.RecordExtType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Document(collection = "demo")
/**
 * 建立索引，提高查询速度
 * collection 这个属性是支持spel的，可以实现动态赋值
 */
@CompoundIndexes(
    {
        /**
         * 1 为指定按升序创建索引，如果你想按降序来创建索引指定为 -1
         */
        @CompoundIndex(name = "id_index", def = "{'id':-1}"),
    })
public class DemoPO {

    @Id
    private String id;

    /**
     * 以下两种都会持久化
     */

    @Field
    @Indexed(unique = true)
    private String name;

    private Point location;

    // 扩展字段
    @Field("ext")
    private Map<String, Object> extValues = Maps.newHashMap();

    // 扩展字段类型
    @Field("ext_t")
    private Map<String, Integer> extTypes = Maps.newHashMap();

    /**
     * 不持久化字段
     */
    @Transient
    private String tttt;

    @CreatedDate
    private Date createTime;

    public static DemoPO of(String id, String name, Point point) {
        DemoPO result = new DemoPO();
        result.id = id;
        result.name = name;
        result.location = point;
        result.tttt = "fdsafds";
        return result;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Point getLocation() {
        return location;
    }

    public String getTttt() {
        return tttt;
    }

    public List<RecordExt> getExts() {
        var result = Lists.<RecordExt>newArrayList();
        for (var extEntry : extValues.entrySet()) {
            var extType = RecordExtType.get(extTypes.get(extEntry.getKey()));
            var value = extType.getValueType().toString(extEntry.getValue());
            result.add(RecordExt.of(extType, value));
        }
        return result;
    }

    public void addExt(RecordExt ext) {
        var type = ext.getType();
        var value = ext.getValue();
        extValues.put(type.getFieldName(StringUtils.EMPTY), type.getValueType().toValue(value));
        extTypes.put(type.getFieldName(StringUtils.EMPTY), type.getId());
    }

}
