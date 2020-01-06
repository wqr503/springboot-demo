package com.cn.lp.domain;

import io.grpc.Internal;
import io.grpc.LoadBalancer;
import io.grpc.LoadBalancerProvider;
import io.grpc.services.HealthCheckingLoadBalancerUtil;

/**
 *
 */
@Internal
public class HealthCheckingWeightLoadBalancerProvider extends LoadBalancerProvider {

    private final LoadBalancerProvider rrProvider;

    public HealthCheckingWeightLoadBalancerProvider() {
        rrProvider = new WeightLoadBalancerProvider();
    }

    @Override
    public boolean isAvailable() {
        return rrProvider.isAvailable();
    }

    @Override
    public int getPriority() {
        return rrProvider.getPriority() + 1;
    }

    @Override
    public String getPolicyName() {
        return rrProvider.getPolicyName();
    }

    @Override
    public LoadBalancer newLoadBalancer(LoadBalancer.Helper helper) {
        return HealthCheckingLoadBalancerUtil.newHealthCheckingLoadBalancer(rrProvider, helper);
    }
}
