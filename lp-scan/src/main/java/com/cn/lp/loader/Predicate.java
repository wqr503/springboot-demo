package com.cn.lp.loader;

/**
 * 断言
 *
 * @param <T>
 */
public interface Predicate<T> {

    /**
     * 根据给定的参数计算此断言
     *
     * @param t
     * @return
     */
    boolean test(T t);

}
