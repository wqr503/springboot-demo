package com.cn.lp.shiro.annotation;

import java.lang.annotation.*;

/**
 * 权限注解
 * Created by qirong on 2018/5/24.
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ShiroDefinition {

    /**
     * 过滤器
     * (anon,authc,authcBasic,perms,port,rest,roles,ssl,user)
     * (authcJwt, anyRole)
     * @return
     */
    String filter() default "authc";

}
