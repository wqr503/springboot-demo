package com.cn.lp.shardingsphere.exception;

/**
 * 没有对应方法异常
 * @author qirong
 */
public class NoSuchMethodException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoSuchMethodException() {
		super();
	}

	public NoSuchMethodException(String message) {
		super(message);
	}

	public NoSuchMethodException(String message, Throwable cause) {
		super(message, cause);
	}
}
