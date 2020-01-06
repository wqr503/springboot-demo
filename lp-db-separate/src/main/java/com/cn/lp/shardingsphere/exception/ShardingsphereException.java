package com.cn.lp.shardingsphere.exception;

/**
 * 散列异常
 * Created by qirong on 2019/5/21.
 */
public class ShardingsphereException extends RuntimeException {

    public ShardingsphereException() {
        super();
    }

    public ShardingsphereException(String message) {
        super(message);
    }
}
