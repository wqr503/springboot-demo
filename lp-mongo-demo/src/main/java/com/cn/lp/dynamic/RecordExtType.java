package com.cn.lp.dynamic;

import java.util.Optional;

/**
 * 记录扩展字段类型
 */
public enum RecordExtType {

    // 通用 ========================================================================

    OPERATOR_ID(1, "操作者id", "operatorID", ExtValueType.LONG),

    OPERATOR_ACCOUNT(2, "操作者账号", "operatorAccount", ExtValueType.STRING),

    OPERATOR_NAME(3, "操作者名称", "operatorName", ExtValueType.STRING),

    TARGET_ID(4, "目标id", "targetID", ExtValueType.LONG),

    TARGET_ACCOUNT(5, "目标账号", "targetAccount", ExtValueType.STRING),

    TARGET_NAME(6, "目标名称", "targetName", ExtValueType.STRING),

    LEVEL(7, "级别", "level", ExtValueType.INT),

    OPERATOR_DESC(8, "操作描述", "operatorDesc", ExtValueType.STRING),

    // 自定义 =============================================================================

    TEST_ATR(100, "测试字段", "testAtr", ExtValueType.STRING),

    ;

    private int id;
    private String desc;
    private String fieldName;
    private ExtValueType valueType;

    RecordExtType(int id, String desc, String fieldName, ExtValueType valueType) {
        this.id = id;
        this.desc = desc;
        this.fieldName = fieldName;
        this.valueType = valueType;
    }

    public int getId() {
        return this.id;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getFieldName(String prefix) {
        return prefix.concat(fieldName);
    }

    /**
     * 值类型
     */
    public ExtValueType getValueType() {
        return valueType;
    }

    /**
     * 通过id获得枚举项
     *
     * @param id 枚举id
     * @throws RuntimeException 当指定id不存在
     */
    public static RecordExtType get(int id) {
        return tryGet(id).orElseThrow(() -> new RuntimeException("[" + RecordExtType.class.getSimpleName() + "]枚举中不存在id为[" + id + "]的项!"));
    }

    /**
     * 尝试通过id获得枚举项
     *
     * @param id 枚举id
     */
    public static Optional<RecordExtType> tryGet(int id) {
        for (var item : RecordExtType.values()) {
            if (item.getId() == id) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

}