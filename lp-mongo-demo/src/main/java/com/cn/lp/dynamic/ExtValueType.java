package com.cn.lp.dynamic;

import java.util.Date;
import java.util.Optional;

/**
 * 记录扩展字段值类型
 */
public enum ExtValueType {

    INT(1, "int", Integer.class) {
        @Override
        public Object toValue(String valueStr) {
            return Integer.valueOf(valueStr);
        }
    },
    LONG(2, "long", Long.class) {
        @Override
        public Object toValue(String valueStr) {
            return Long.valueOf(valueStr);
        }
    },
    FLOAT(3, "float", Float.class) {
        @Override
        public Object toValue(String valueStr) {
            return Float.valueOf(valueStr);
        }
    },
    DOUBLE(4, "double", Double.class) {
        @Override
        public Object toValue(String valueStr) {
            return Double.valueOf(valueStr);
        }
    },
    BOOLEAN(5, "boolean", Boolean.class) {
        @Override
        public Object toValue(String valueStr) {
            return Boolean.valueOf(valueStr);
        }
    },
    STRING(6, "string", String.class) {
        @Override
        public Object toValue(String valueStr) {
            return valueStr;
        }
    },
    DATE(7, "date", Date.class) {
        @Override
        public Object toValue(String valueStr) {
            var t = Long.parseLong(valueStr);
            return new Date(t);
        }

        @Override
        public String toString(Object object) {
            var date = (Date) object;
            var t = date.getTime();
            return String.valueOf(t);
        }
    },

    ;
    private int id;
    private String desc;
    private Class<?> clazz;

    ExtValueType(int id, String desc, Class<?> clazz) {
        this.id = id;
        this.desc = desc;
        this.clazz = clazz;
    }

    public int getId() {
        return this.id;
    }

    public String getDesc() {
        return this.desc;
    }

    public Class<?> getValueClass() {
        return clazz;
    }

    public boolean check(Object val) {
        return clazz.isAssignableFrom(val.getClass());
    }

    /**
     * 通过id获得枚举项
     *
     * @param id 枚举id
     * @throws RuntimeException 当指定id不存在
     */
    public static ExtValueType get(int id) {
        return tryGet(id).orElseThrow(() -> new RuntimeException("[" + ExtValueType.class.getSimpleName() + "]枚举中不存在id为[" + id + "]的项!"));
    }

    /**
     * 尝试通过id获得枚举项
     *
     * @param id 枚举id
     */
    public static Optional<ExtValueType> tryGet(int id) {
        for (var item : ExtValueType.values()) {
            if (item.getId() == id) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public String toString(Object object) {
        return object.toString();
    }

    public abstract Object toValue(String valueStr);

}