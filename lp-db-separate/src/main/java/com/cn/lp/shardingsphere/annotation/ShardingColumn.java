package com.cn.lp.shardingsphere.annotation;

import java.lang.annotation.*;

/**
 * 散列字段
 * 
 * @author KGTny
 * 
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ShardingColumn {

    String name() default "";

}
