package com.cn.lp.qrcard.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;

/**
 * 域名过滤器，添加GateWay域名到Request中给下流服务器使用
 * Created by qirong on 2019/5/27.
 */
@Component
public class RealmNameGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private static final Logger logger = LoggerFactory.getLogger(RealmNameGatewayFilterFactory.class);

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest host = exchange.getRequest().mutate().headers(httpHeaders -> {
                URI requestURI = exchange.getRequest().getURI();
                String realmName = requestURI.getHost();
                Assert.notNull(realmName, "host 为空");
                realmName = realmName + ":" + requestURI.getPort();
                httpHeaders.add("realmName", realmName);
                logger.info("headers:" + httpHeaders.toString());
            }).build();
            //将现在的request 变成 change对象
            ServerWebExchange build = exchange.mutate().request(host).build();
            return chain.filter(build);
        };
    }

}