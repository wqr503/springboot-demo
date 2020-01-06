package com.cn.lp.domain;

import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 *
 */
public class ExceptionUtil {

//    一条限流规则主要由下面几个因素组成：
//
//    resource：资源名，即限流规则的作用对象
//    count:限流阈值
//    grade:限流阈值类型（QPS 或并发线程数）
//    limitApp:流控针对的调用来源，若为 default 则不区分调用来源
//    strategy:调用关系限流策略
//    controlBehavior:流量控制效果（直接拒绝、Warm Up、匀速排队）
//    SpringCloud alibaba集成Sentinel后只需要在配置文件中进行相关配置，即可在 Spring容器中自动注册 DataSource，这点很方便。配置文件添加如下配置

//    blockHandlerClass中对应的异常处理方法名。参数类型和返回值必须和原方法一致
    public static String handleException(BlockException ex) {
        return "Oops: " + ex.getClass().getCanonicalName() + "-" + "通过@SentinelResource注解配置限流埋点并自定义处理限流后的逻辑";
    }

}
