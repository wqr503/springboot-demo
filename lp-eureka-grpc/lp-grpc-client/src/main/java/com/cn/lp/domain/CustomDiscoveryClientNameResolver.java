package com.cn.lp.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.Status;
import io.grpc.internal.SharedResourceHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.CollectionUtils;

import javax.annotation.concurrent.GuardedBy;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 *
 */
public class CustomDiscoveryClientNameResolver extends NameResolver {

    private Logger logger = LoggerFactory.getLogger(CustomDiscoveryClientNameResolver.class);

    private final String name;
    private final DiscoveryClient client;
    private final SharedResourceHolder.Resource<ScheduledExecutorService> timerServiceResource;
    private final SharedResourceHolder.Resource<Executor> executorResource;
    @GuardedBy("this")
    private boolean shutdown;
    @GuardedBy("this")
    private ScheduledExecutorService timerService;
    @GuardedBy("this")
    private Executor executor;
    @GuardedBy("this")
    private ScheduledFuture<?> resolutionTask;
    @GuardedBy("this")
    private boolean resolving;
    @GuardedBy("this")
    private Listener listener;
    @GuardedBy("this")
    private List<ServiceInstance> serviceInstanceList;

    public CustomDiscoveryClientNameResolver(final String name, final DiscoveryClient client,
        final SharedResourceHolder.Resource<ScheduledExecutorService> timerServiceResource,
        final SharedResourceHolder.Resource<Executor> executorResource) {
        this.name = name;
        this.client = client;
        this.timerServiceResource = timerServiceResource;
        this.executorResource = executorResource;
        this.serviceInstanceList = Lists.newArrayList();
    }

    @Override
    public final String getServiceAuthority() {
        return name;
    }

    @Override
    public final synchronized void start(Listener listener) {
        Preconditions.checkState(this.listener == null, "already started");
        timerService = SharedResourceHolder.get(timerServiceResource);
        executor = SharedResourceHolder.get(executorResource);
        this.listener = Preconditions.checkNotNull(listener, "listener");
        resolve();
    }

    @Override
    public final synchronized void refresh() {
        if (listener != null) {
            resolve();
        }
    }

    private final Runnable resolutionRunnable = new Runnable() {
        @Override
        public void run() {
            Listener savedListener;
            synchronized (CustomDiscoveryClientNameResolver.this) {
                // If this task is started by refresh(), there might already be a scheduled task.
                if (resolutionTask != null) {
                    resolutionTask.cancel(false);
                    resolutionTask = null;
                }
                if (shutdown) {
                    return;
                }
                savedListener = listener;
                resolving = true;
            }
            try {
                List<ServiceInstance> newServiceInstanceList;
                try {
                    newServiceInstanceList = client.getInstances(name);
                } catch (Exception e) {
                    savedListener.onError(Status.UNAVAILABLE.withCause(e));
                    return;
                }

                if (!CollectionUtils.isEmpty(newServiceInstanceList)) {
                    if (isNeedToUpdateServiceInstanceList(newServiceInstanceList)) {
                        serviceInstanceList = newServiceInstanceList;
                    } else {
                        return;
                    }
                    List<EquivalentAddressGroup> equivalentAddressGroups = Lists.newArrayList();
                    for (ServiceInstance serviceInstance : serviceInstanceList) {
                        Map<String, String> metadata = serviceInstance.getMetadata();
                        if (metadata.get("gRPC.port") != null) {
                            Integer port = Integer.valueOf(metadata.get("gRPC.port"));
                            logger.info("Found gRPC server {} {}:{}", name, serviceInstance.getHost(), port);
                            Attributes.Builder builder = Attributes.newBuilder();
                            if (metadata.get(WeightLoadBalancer.WEIGHT_KEY.toString()) != null) {
                                Integer weight = Integer.valueOf(metadata.get(WeightLoadBalancer.WEIGHT_KEY.toString()));
                                builder.set(WeightLoadBalancer.WEIGHT_KEY, weight);
                            }
                            EquivalentAddressGroup addressGroup = new EquivalentAddressGroup(
                                new InetSocketAddress(serviceInstance.getHost(), port), builder.build());
                            equivalentAddressGroups.add(addressGroup);
                        } else {
                            logger.error("Can not found gRPC server {}", name);
                        }
                    }
                    savedListener.onAddresses(equivalentAddressGroups, Attributes.EMPTY);
                } else {
                    savedListener.onError(Status.UNAVAILABLE
                        .withDescription("No servers found for " + name));
                }
            } finally {
                synchronized (CustomDiscoveryClientNameResolver.this) {
                    resolving = false;
                }
            }
        }
    };

    private boolean isNeedToUpdateServiceInstanceList(List<ServiceInstance> newServiceInstanceList) {
        if (serviceInstanceList.size() == newServiceInstanceList.size()) {
            for (ServiceInstance serviceInstance : serviceInstanceList) {
                boolean isSame = false;
                for (ServiceInstance newServiceInstance : newServiceInstanceList) {
                    if (newServiceInstance.getHost().equals(serviceInstance.getHost())
                        && newServiceInstance.getPort() == serviceInstance.getPort()) {
                        isSame = true;
                        break;
                    }
                }
                if (!isSame) {
                    logger.info("Ready to update {} server info group list", name);
                    return true;
                }
            }
        } else {
            logger.info("Ready to update {} server info group list", name);
            return true;
        }
        return false;
    }

    @GuardedBy("this")
    private void resolve() {
        if (resolving || shutdown) {
            return;
        }
        executor.execute(resolutionRunnable);
    }

    @Override
    public void shutdown() {
        if (shutdown) {
            return;
        }
        shutdown = true;
        if (resolutionTask != null) {
            resolutionTask.cancel(false);
        }
        if (timerService != null) {
            timerService = SharedResourceHolder.release(timerServiceResource, timerService);
        }
        if (executor != null) {
            executor = SharedResourceHolder.release(executorResource, executor);
        }
    }

    @Override
    public String toString() {
        return "CustomDiscoveryClientNameResolver [name=" + name + ", discoveryClient=" + client + "]";
    }

}
