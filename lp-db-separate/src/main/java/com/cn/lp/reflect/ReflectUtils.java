package com.cn.lp.reflect;

import com.cn.lp.shardingsphere.exception.NoSuchMethodException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 反射工具
 *
 * @author qirong
 */
public class ReflectUtils {

    /**
     * @author KGTny
     */
    public enum MethodType {

        /**
         * @uml.property name="getter"
         * @uml.associationEnd
         */
        Getter("get", "is"),

        /**
         * @uml.property name="setter"
         * @uml.associationEnd
         */
        Setter("set");

        public final String[] values;

        MethodType(String... values) {
            this.values = values;
        }

    }

    /**
     * 获取接口
     *
     * @param clazz
     * @return
     */
    public static List<Class<?>> getDeepInterface(Class<?> clazz) {
        List<Class<?>> interfaceClassList = new ArrayList<>();
        for (Class<?> interfacer : clazz.getInterfaces()) {
            interfaceClassList.add(interfacer);
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class && superClass != null) {
            interfaceClassList.addAll(getDeepInterface(superClass));
        }
        return interfaceClassList;
    }

    /**
     * 获取指定属性
     *
     * @param clazz
     * @param name
     * @return
     */
    public static Field getDeepField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            return field;
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != Object.class) {
                return getDeepField(superClass, name);
            }
        }
        return null;
    }

    /**
     * 获取参数
     *
     * @param clazz
     * @return
     */
    public static List<Field> getDeepField(Class<?> clazz) {
        return getDeepField(clazz, Object.class);
    }

    /**
     * 获取参数
     *
     * @param clazz
     * @param endClazz 终止类
     * @return
     */
    public static List<Field> getDeepField(Class<?> clazz, Class<?> endClazz) {
        List<Field> fieldList = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            fieldList.add(field);
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class && superClass != endClazz) {
            fieldList.addAll(getDeepField(superClass));
        }
        return fieldList;
    }

    /**
     * 获取所有该注解的属性
     *
     * @param clazz
     * @param annotation
     * @return
     */
    public static List<Field> getDeepFieldByAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Field> fieldList = new ArrayList<Field>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getAnnotation(annotation) != null) {
                field.setAccessible(true);
                fieldList.add(field);
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class) {
            fieldList.addAll(getDeepFieldByAnnotation(superClass, annotation));
        }
        return fieldList;
    }

    /**
     * 获取所有该注解的方法
     *
     * @param clazz
     * @param annotation
     * @return
     */
    public static List<Method> getDeepMethodByAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Method> methodList = new ArrayList<Method>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(annotation) != null) {
                method.setAccessible(true);
                methodList.add(method);
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class) {
            methodList
                .addAll(getDeepMethodByAnnotation(superClass, annotation));
        }
        return methodList;
    }

    /**
     * 获取所有方法(包括父类，但是不包括Object的方法),获取到的方法不包括继承的方法
     *
     * @param clazz
     * @return
     */
    public static List<Method> getDeepMethod(Class<?> clazz) {
        List<Method> methodList = new ArrayList<Method>();
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);
            methodList.add(method);
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            methodList.addAll(getDeepMethod(superClass));
        }
        return methodList;
    }

    /**
     * 获取指定方法(包括父类，但是不包括Object的方法),获取到的方法不包括继承的方法
     *
     * @param clazz
     * @param name
     * @param paramType
     * @return
     */
    public static Method getDeepMethod(Class<?> clazz, String name,
        Class<?>... paramType) {
        try {
            Method method = clazz.getDeclaredMethod(name, paramType);
            method.setAccessible(true);
            return method;
        } catch (Exception e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != Object.class) {
                return getDeepMethod(superClass, name, paramType);
            }
            throw new NoSuchMethodException(
                "ReflectUtils.getPropertyMethod [clazz: " + clazz
                    + ", name: " + name + ", paramType: "
                    + Arrays.toString(paramType) + "] exception");
        }
    }

    /**
     * 获取属性方法
     *
     * @param clazz
     * @param methodType
     * @param paramType
     * @return
     */
    public static Method getPropertyMethod(Class<?> clazz, MethodType methodType, String propertyName, Class<?>... paramType) {
        Method method = null;
        for (String value : methodType.values) {
            if (method == null) {
                try {
                    method = clazz.getDeclaredMethod(parseMethodName(value, propertyName), paramType);
                    if (method != null) {
                        method.setAccessible(true);
                        Class<?> returnClazz = method.getReturnType();
                        if (value.equals("is") && returnClazz != boolean.class
                            && returnClazz == Boolean.class) {
                            method = null;
                        } else {
                            return method;
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
        if (method == null) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != Object.class) {
                try {
                    return getPropertyMethod(superClass, methodType, propertyName, paramType);
                } catch (Exception e) {
                    throw new NoSuchMethodException(
                        "ReflectUtils.getPropertyMethod [clazz: " + clazz
                            + ", MethodType: " + methodType
                            + ", name: " + propertyName + ", paramType: "
                            + Arrays.toString(paramType)
                            + "] exception", e);
                }
            } else {
                throw new NoSuchMethodException(
                    "ReflectUtils.getPropertyMethod [clazz: " + clazz
                        + ", MethodType: " + methodType + ", name: "
                        + propertyName + ", paramType: "
                        + Arrays.toString(paramType) + "] exception");
            }
        }
        return method;
    }

    /**
     * 获取属性方法
     *
     * @param clazz
     * @param methodType
     * @param names
     * @param paramTypes
     * @return
     */
    public static List<Method> getPropertyMethod(Class<?> clazz,
        MethodType methodType, String[] names, Class<?>[][] paramTypes) {
        List<Method> methodList = new ArrayList<Method>();
        for (int index = 0; index < names.length; index++) {
            String name = names[index];
            Class<?>[] params = null;
            if (paramTypes != null && index < paramTypes.length) {
                params = paramTypes[index];
            }
            Method method = getPropertyMethod(clazz, methodType, name, params);
            if (method != null) {
                methodList.add(method);
            }
        }
        return methodList;
    }

    /**
     * 首字母大写
     *
     * @param head
     * @param name
     * @return
     */
    protected static String parseMethodName(String head, String name) {
        return head + name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
