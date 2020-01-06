package com.cn.lp.test.domain.exception;

/**
 * 自定义权限异常
 * Created by qirong on 2019/6/3.
 */
public class AuthException extends RuntimeException {

    public AuthException() {
        super();
    }

    public AuthException(String msg) {
        super(msg);
    }

    public AuthException(Throwable throwable) {
        super(throwable);
    }

}
