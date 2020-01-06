package com.cn.lp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常拦截
 */
@ControllerAdvice
public class WebExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WebExceptionHandler.class);

    /**
     * 参数校验统一异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public String handleBindException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        LOG.warn("参数校验异常:{}({})", fieldError.getDefaultMessage(), fieldError.getField());
        return fieldError.getDefaultMessage();
    }

}
