package com.cn.lp.domain;

import io.grpc.LoadBalancer;
import io.grpc.LoadBalancerProvider;

/**
 *
 */
public class WeightLoadBalancerFactory extends LoadBalancer.Factory {

    private static WeightLoadBalancerFactory instance;

    private final LoadBalancerProvider provider;

    private WeightLoadBalancerFactory() {
        provider = new HealthCheckingWeightLoadBalancerProvider();
    }

    /**
     * Gets the singleton instance of this factory.
     */
    public static synchronized WeightLoadBalancerFactory getInstance() {
        if (instance == null) {
            instance = new WeightLoadBalancerFactory();
        }
        return instance;
    }

    @Override
    public LoadBalancer newLoadBalancer(LoadBalancer.Helper helper) {
        return provider.newLoadBalancer(helper);
    }
}
