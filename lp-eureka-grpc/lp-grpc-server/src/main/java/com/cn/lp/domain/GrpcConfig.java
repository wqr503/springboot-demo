package com.cn.lp.domain;

import com.alibaba.csp.sentinel.adapter.grpc.SentinelGrpcServerInterceptor;
import net.devh.boot.grpc.server.interceptor.GlobalServerInterceptorConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
public class GrpcConfig {

    @Bean
    public GlobalServerInterceptorConfigurer globalInterceptorConfigurerAdapter() {
        return registry -> {
            registry.addServerInterceptors(new SentinelGrpcServerInterceptor());
//            registry.addServerInterceptors()
        };
    }

}
