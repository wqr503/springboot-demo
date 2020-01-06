package com.cn.lp.domain;

import io.grpc.Attributes;
import io.grpc.NameResolver;
import io.grpc.internal.GrpcUtil;
import net.devh.boot.grpc.client.nameresolver.DiscoveryClientResolverFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.client.discovery.event.HeartbeatMonitor;
import org.springframework.context.event.EventListener;

import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class CustomDiscoveryClientResolverFactory extends DiscoveryClientResolverFactory {

    private final Collection<CustomDiscoveryClientNameResolver> customDiscoveryClientNameResolvers = new ArrayList<>();

    private final HeartbeatMonitor customMonitor = new HeartbeatMonitor();

    private final DiscoveryClient customClient;

    /**
     * Creates a new discovery client based name resolver factory.
     *
     * @param client The client to use for the address discovery.
     */
    public CustomDiscoveryClientResolverFactory(final DiscoveryClient client) {
        super(client);
        this.customClient = client;
    }

    @Nullable
    @Override
    public NameResolver newNameResolver(final URI targetUri, final Attributes params) {
        if (DISCOVERY_SCHEME.equals(targetUri.getScheme())) {
            final String serviceName = targetUri.getPath();
            if (serviceName == null || serviceName.length() <= 1 || !serviceName.startsWith("/")) {
                throw new IllegalArgumentException("Incorrectly formatted target uri; "
                    + "expected: '" + DISCOVERY_SCHEME + ":[//]/<service-name>'; "
                    + "but was '" + targetUri.toString() + "'");
            }
            final CustomDiscoveryClientNameResolver discoveryClientNameResolver =
                new CustomDiscoveryClientNameResolver(serviceName.substring(1), this.customClient,
                    GrpcUtil.TIMER_SERVICE, GrpcUtil.SHARED_CHANNEL_EXECUTOR
                );
            this.customDiscoveryClientNameResolvers.add(discoveryClientNameResolver);
            return discoveryClientNameResolver;
        }
        return null;
    }

    /**
     * Triggers a refresh of the registered name resolvers.
     *
     * @param event The event that triggered the update.
     */
    @EventListener(HeartbeatEvent.class)
    @Override
    public void heartbeat(final HeartbeatEvent event) {
        if (this.customMonitor.update(event.getValue())) {
            for (final CustomDiscoveryClientNameResolver discoveryClientNameResolver : this.customDiscoveryClientNameResolvers) {
                discoveryClientNameResolver.refresh();
            }
        }
    }

    /**
     * Cleans up the name resolvers.
     */
    @PreDestroy
    @Override
    public void destroy() {
        this.customDiscoveryClientNameResolvers.clear();
    }

    @Override
    public String toString() {
        return "CustomDiscoveryClientResolverFactory [scheme=" + getDefaultScheme() +
            ", discoveryClient=" + this.customClient + "]";
    }

}