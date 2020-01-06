package com.cn.lp.dynamic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Document
public class Record {

    public static final String RECORD_ID = "recordID";

    public static final String CREATE_TIME = "createTime";

    /**
     * 记录ID
     */
    @Indexed(unique = true)
    @Field(RECORD_ID)
    private long recordID;

    /**
     * 扩展字段，为了擦除类型，所以字段全存为string
     */
    @Field("ext")
    private Map<String, Object> extValues;

    /**
     * 扩展字段类型，记录被擦除的字段类型
     */
    @Field("ext_t")
    private Map<String, Integer> extTypes;

    /**
     * 为了效率，让mongodb随机生成id
     * 但是生成的ID是ObjectID("5d37f3f75d084d0fc48a7bec")
     * 所以我们使用recordID字段作为我们记录ID
     */
    @Id
    private String id;

    /**
     * 创建时间
     */
    @Field(CREATE_TIME)
    @CreatedDate
    private Date createTime;

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Record setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public static Record of(long recordID) {
        var result = new Record();
        result.recordID = recordID;
        result.extValues = Maps.newHashMap();
        result.extTypes = Maps.newHashMap();
        return result;
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

    public Record addExt(RecordExt ext) {
        var type = ext.getType();
        var value = ext.getValue();
        extValues.put(type.getFieldName(StringUtils.EMPTY), type.getValueType().toValue(value));
        extTypes.put(type.getFieldName(StringUtils.EMPTY), type.getId());
        return this;
    }

    public long getRecordID() {
        return recordID;
    }
}
