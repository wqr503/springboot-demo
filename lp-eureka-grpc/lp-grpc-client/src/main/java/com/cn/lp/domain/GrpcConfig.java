package com.cn.lp.domain;

import com.alibaba.csp.sentinel.adapter.grpc.SentinelGrpcClientInterceptor;
import io.grpc.LoadBalancer;
import net.devh.boot.grpc.client.interceptor.GlobalClientInterceptorConfigurer;
import net.devh.boot.grpc.client.nameresolver.DiscoveryClientResolverFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 *
 */
@Configuration
public class GrpcConfig {

    @Bean
    public GlobalClientInterceptorConfigurer globalInterceptorConfigurerAdapter() {
        return registry -> {
            registry.addClientInterceptors(new SentinelGrpcClientInterceptor());
//            registry.addServerInterceptors()
        };
    }

    @Lazy // Not needed for InProcessChannelFactories
    @Bean
    @SuppressWarnings("deprecation") // Required to stay compatible with pre 1.17.0 versions
    public LoadBalancer.Factory grpcLoadBalancerFactory() {
        return WeightLoadBalancerFactory.getInstance();
//        return io.grpc.util.RoundRobinLoadBalancerFactory.getInstance();
        // return LoadBalancerRegistry.getDefaultRegistry().getProvider("round_robin");
    }

    @Lazy // Not needed for InProcessChannelFactories
    @Bean
    DiscoveryClientResolverFactory grpcDiscoveryClientResolverFactory(final DiscoveryClient client) {
        return new CustomDiscoveryClientResolverFactory(client);
    }

}
