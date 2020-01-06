package com.cn.lp.dynamic;

import java.util.Optional;

/**
 * 条件类型
 * @author ly-345
 */
public enum ConditionType {

    EQ(1, "等于"),

    NOT_EQ(2, "不等于"),

    GT(3, "大于"),

    GE(4, "大于等于"),

    LT(5, "小于"),

    LE(6, "小于等于"),

    IN(7, "在...中"),

    NOT_IN(8, "不在...中"),

    LIKE(9, "模糊搜索"),

    ;
    private int id;
    private String desc;

    ConditionType(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public int getId() {
        return this.id;
    }

    public String getDesc() {
        return this.desc;
    }

    /**
     * 通过id获得枚举项
     *
     * @param id 枚举id
     * @throws RuntimeException 当指定id不存在
     */
    public static ConditionType get(int id) {
        return tryGet(id).orElseThrow(() -> new RuntimeException("[" + ConditionType.class.getSimpleName() + "]枚举中不存在id为[" + id + "]的项!"));
    }

    /**
     * 尝试通过id获得枚举项
     *
     * @param id 枚举id
     */
    public static Optional<ConditionType> tryGet(int id) {
        for (var item : ConditionType.values()) {
            if (item.getId() == id) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

}