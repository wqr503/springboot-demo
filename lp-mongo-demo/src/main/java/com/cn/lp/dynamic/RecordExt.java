package com.cn.lp.dynamic;


public class RecordExt {

    /**
     * 扩展字段类型
     */
    private RecordExtType type;
    /**
     * 扩展字段值
     */
    private String value;

    public static RecordExt of(RecordExtType type, String value) {
        var result = new RecordExt();
        result.type = type;
        result.value = value;
        return result;
    }

    public RecordExtType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
