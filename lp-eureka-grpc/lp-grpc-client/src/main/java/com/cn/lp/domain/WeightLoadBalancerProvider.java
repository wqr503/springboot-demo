package com.cn.lp.domain;

import io.grpc.LoadBalancer;
import io.grpc.LoadBalancerProvider;

/**
 *
 */
public class WeightLoadBalancerProvider extends LoadBalancerProvider {

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public String getPolicyName() {
        return "weight";
    }

    @Override
    public LoadBalancer newLoadBalancer(LoadBalancer.Helper helper) {
        return new WeightLoadBalancer(helper);
    }
}