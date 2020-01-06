package com.cn.lp.domain;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import io.grpc.*;
import io.grpc.internal.GrpcAttributes;
import io.grpc.internal.ServiceConfigUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.grpc.ConnectivityState.*;

/**
 *
 */
public class WeightLoadBalancer extends LoadBalancer {

    public static final Attributes.Key<Integer> WEIGHT_KEY = Attributes.Key.create("grpc_weight");

    @VisibleForTesting
    static final Attributes.Key<Ref<ConnectivityStateInfo>> STATE_INFO =
        Attributes.Key.create("state-info");

    /**
     * 粘性依赖
     */
    static final Attributes.Key<Ref<Subchannel>> STICKY_REF = Attributes.Key.create("sticky-ref");

    @Nullable
    private StickinessState stickinessState;

    private ConnectivityState currentState;

    /**
     * 没有已准备好子通道
     */
    private static final Status EMPTY_OK = Status.OK.withDescription("no subchannels ready");

    /**
     * key: 地址组合
     * value: 通道
     */
    private final Map<EquivalentAddressGroup, Subchannel> subchannels =
        new HashMap<EquivalentAddressGroup, Subchannel>();

    private final Helper helper;

    private WeightLoadBalancer.WeightPicker currentPicker = new WeightLoadBalancer.EmptyPicker(EMPTY_OK);

    private final Random random;

    WeightLoadBalancer(Helper helper) {
        this.helper = checkNotNull(helper, "helper");
        this.random = new Random();
    }

    /**
     * 处理名称解析系统中新解析的服务器组和元数据属性
     *
     * @param servers
     * @param attributes
     */
    @Override
    public void handleResolvedAddressGroups(List<EquivalentAddressGroup> servers, Attributes attributes) {
        Set<EquivalentAddressGroup> currentAddrs = subchannels.keySet();
        Set<EquivalentAddressGroup> latestAddrs = stripAttrs(servers);
        Set<EquivalentAddressGroup> addedAddrs = servers.stream()
            .filter(addressGroup -> !(currentAddrs.contains(new EquivalentAddressGroup(addressGroup.getAddresses()))))
            .collect(Collectors.toSet());
        Set<EquivalentAddressGroup> removedAddrs = setsDifference(currentAddrs, latestAddrs);

        // 获取属性
        Map<String, Object> serviceConfig =
            attributes.get(GrpcAttributes.NAME_RESOLVER_SERVICE_CONFIG);

        if (serviceConfig != null) {
            // 从服务配置中提取上下文元数据键。
            String stickinessMetadataKey =
                ServiceConfigUtil.getStickinessMetadataKeyFromServiceConfig(serviceConfig);
            if (stickinessMetadataKey != null) {
                // 二进制头后缀
                if (stickinessMetadataKey.endsWith(Metadata.BINARY_HEADER_SUFFIX)) {
                    helper.getChannelLogger().log(
                        ChannelLogger.ChannelLogLevel.WARNING,
                        "Binary stickiness header is not supported. The header \"{0}\" will be ignored",
                        stickinessMetadataKey
                    );
                } else if (stickinessState == null
                    || !stickinessState.key.name().equals(stickinessMetadataKey)) {
                    // 绑定上下文状态
                    stickinessState = new WeightLoadBalancer.StickinessState(stickinessMetadataKey);
                }
            }
        }

        // Create new subchannels for new addresses.
        // 新增的地址
        for (EquivalentAddressGroup addressGroup : addedAddrs) {
            // NB(lukaszx0): we don't merge `attributes` with `subchannelAttr` because subchannel
            // doesn't need them. They're describing the resolved server list but we're not taking
            // any action based on this information.
            // 子通道属性
            Attributes.Builder subchannelAttrs = Attributes.newBuilder()
                // NB(lukaszx0): because attributes are immutable we can't set new value for the key
                // after creation but since we can mutate the values we leverage that and set
                // AtomicReference which will allow mutating state info for given channel.
                .set(
                    STATE_INFO,
                    new Ref<ConnectivityStateInfo>(ConnectivityStateInfo.forNonError(IDLE))
                );

            subchannelAttrs.setAll(addressGroup.getAttributes());

            // 绑定上下文
            Ref<Subchannel> stickyRef = null;
            if (stickinessState != null) {
                subchannelAttrs.set(STICKY_REF, stickyRef = new Ref<Subchannel>(null));
            }

            // 根据地址建立通道
            Subchannel subchannel = checkNotNull(
                helper.createSubchannel(addressGroup, subchannelAttrs.build()), "subchannel");

            if (stickyRef != null) {
                // 绑定通道
                stickyRef.value = subchannel;
            }

            subchannels.put(addressGroup, subchannel);
            //请求子通道创建连接(即传输)
            subchannel.requestConnection();
        }

        // Shutdown subchannels for removed addresses.
        // 移除废弃地址的通道
        for (EquivalentAddressGroup addressGroup : removedAddrs) {
            Subchannel subchannel = subchannels.remove(addressGroup);
            shutdownSubchannel(subchannel);
        }

        // 更新状态
        updateBalancingState();
    }

    /**
     * 处理来自名称解析系统的错误
     * TRANSIENT_FAILURE 短暂失败
     *
     * @param error
     */
    @Override
    public void handleNameResolutionError(Status error) {
        updateBalancingState(
            TRANSIENT_FAILURE,
            currentPicker instanceof WeightLoadBalancer.ReadyPicker ? currentPicker : new WeightLoadBalancer.EmptyPicker(error)
        );
    }

    /**
     * 处理子通道上的状态更改
     *
     * @param subchannel
     * @param stateInfo
     */
    @Override
    public void handleSubchannelState(Subchannel subchannel, ConnectivityStateInfo stateInfo) {
        if (subchannels.get(subchannel.getAddresses()) != subchannel) {
            return;
        }
        if (stateInfo.getState() == SHUTDOWN && stickinessState != null) {
            stickinessState.remove(subchannel);
        }
        if (stateInfo.getState() == IDLE) {
            subchannel.requestConnection();
        }
        getSubchannelStateInfoRef(subchannel).value = stateInfo;
        updateBalancingState();
    }

    /**
     * Updates picker with the list of active subchannels (state == READY).
     */
    @SuppressWarnings("ReferenceEquality")
    private void updateBalancingState() {
        List<Subchannel> activeList = filterNonFailingSubchannels(getSubchannels());
        if (activeList.isEmpty()) {
            boolean isConnecting = false;
            Status aggStatus = EMPTY_OK;
            for (Subchannel subchannel : getSubchannels()) {
                //获取链接状态信息
                // ConnectivityState 通道链接状态
                // Status 对于客户端，每一个远程调用在完成时会返回一个状态
                ConnectivityStateInfo stateInfo = getSubchannelStateInfoRef(subchannel).value;
                // This subchannel IDLE is not because of channel IDLE_TIMEOUT,
                // in which case LB is already shutdown.
                // RRLB will request connection immediately on subchannel IDLE.
                // IDLE 闲置状态
                if (stateInfo.getState() == CONNECTING || stateInfo.getState() == IDLE) {
                    isConnecting = true;
                }
                if (aggStatus == EMPTY_OK || !aggStatus.isOk()) {
                    aggStatus = stateInfo.getStatus();
                }
            }
            updateBalancingState(
                isConnecting ? CONNECTING : TRANSIENT_FAILURE,
                // If all subchannels are TRANSIENT_FAILURE, return the Status associated with
                // an arbitrary subchannel, otherwise return OK.
                new WeightLoadBalancer.EmptyPicker(aggStatus)
            );
        } else {
            // initialize the Picker to a random start index to ensure that a high frequency of Picker
            // churn does not skew subchannel selection.
            // 将选择器初始化为随机启动索引，以确保选择器的高频率
            int startIndex = random.nextInt(activeList.size());
            updateBalancingState(READY, new WeightLoadBalancer.ReadyPicker(activeList, startIndex, stickinessState));
        }
    }

    /**
     * 排除a列表中b的相同元素
     *
     * @param a
     * @param b
     * @param <T>
     * @return
     */
    private static <T> Set<T> setsDifference(Set<T> a, Set<T> b) {
        Set<T> aCopy = new HashSet<T>(a);
        aCopy.removeAll(b);
        return aCopy;
    }

    /**
     * 获取地址组合列表
     * Converts list of {@link EquivalentAddressGroup} to {@link EquivalentAddressGroup} set and
     * remove all attributes.
     */
    private static Set<EquivalentAddressGroup> stripAttrs(List<EquivalentAddressGroup> groupList) {
        Set<EquivalentAddressGroup> addrs = new HashSet<EquivalentAddressGroup>(groupList.size());
        for (EquivalentAddressGroup group : groupList) {
            addrs.add(new EquivalentAddressGroup(group.getAddresses()));
        }
        return addrs;
    }

    /**
     * 过滤未准备好子通道
     * Filters out non-ready subchannels.
     */
    private static List<Subchannel> filterNonFailingSubchannels(
        Collection<Subchannel> subchannels) {
        List<Subchannel> readySubchannels = new ArrayList<>(subchannels.size());
        for (Subchannel subchannel : subchannels) {
            if (isReady(subchannel)) {
                readySubchannels.add(subchannel);
            }
        }
        return readySubchannels;
    }

    /**
     * 更新平衡状态
     *
     * @param state  链接状态
     * @param picker 选择器
     */
    private void updateBalancingState(ConnectivityState state, WeightLoadBalancer.WeightPicker picker) {
        if (state != currentState || !picker.isEquivalentTo(currentPicker)) {
            //Set a new state with a new picker to the channel.
            helper.updateBalancingState(state, picker);
            currentState = state;
            currentPicker = picker;
        }
    }

    /**
     * 权重平衡逻辑
     */
    private abstract static class WeightPicker extends SubchannelPicker {

        abstract boolean isEquivalentTo(WeightLoadBalancer.WeightPicker picker);

    }

    @Override
    public void shutdown() {
        for (Subchannel subchannel : getSubchannels()) {
            shutdownSubchannel(subchannel);
        }
    }

    @VisibleForTesting
    Collection<Subchannel> getSubchannels() {
        return subchannels.values();
    }

    private void shutdownSubchannel(Subchannel subchannel) {
        subchannel.shutdown();
        getSubchannelStateInfoRef(subchannel).value =
            ConnectivityStateInfo.forNonError(SHUTDOWN);
        if (stickinessState != null) {
            stickinessState.remove(subchannel);
        }
    }

    /**
     * 获取子通道上下文
     *
     * @param subchannel
     * @return
     */
    private static Ref<ConnectivityStateInfo> getSubchannelStateInfoRef(
        Subchannel subchannel) {
        return checkNotNull(subchannel.getAttributes().get(STATE_INFO), "STATE_INFO");
    }

    /**
     * A lighter weight Reference than AtomicReference.
     */
    @VisibleForTesting
    static final class Ref<T> {

        T value;

        Ref(T value) {
            this.value = value;
        }

    }

    static boolean isReady(Subchannel subchannel) {
        return getSubchannelStateInfoRef(subchannel).value.getState() == READY;
    }


    /**
     * 无链接可用时的空选择器
     */
    @VisibleForTesting
    static final class EmptyPicker extends WeightLoadBalancer.WeightPicker {

        /**
         * 对于客户端，每一个远程调用在完成时会返回一个状态
         */
        private final Status status;

        EmptyPicker(@Nonnull Status status) {
            this.status = Preconditions.checkNotNull(status, "status");
        }

        /**
         * 选择子通道
         * PickResult 选择结果
         *
         * @param args 选择子通道属性
         * @return
         */
        @Override
        public PickResult pickSubchannel(PickSubchannelArgs args) {
            return status.isOk() ? PickResult.withNoResult() : PickResult.withError(status);
        }

        /**
         * 是否等效
         *
         * @param picker
         * @return
         */
        @Override
        boolean isEquivalentTo(WeightLoadBalancer.WeightPicker picker) {
            return picker instanceof WeightLoadBalancer.EmptyPicker && (Objects.equal(status, ((WeightLoadBalancer.EmptyPicker) picker).status)
                || (status.isOk() && ((WeightLoadBalancer.EmptyPicker) picker).status.isOk()));
        }
    }

    /**
     * 已准备好的选择器
     */
    @VisibleForTesting
    static final class ReadyPicker extends WeightLoadBalancer.WeightPicker {

        /**
         * 基于反射的实用工具，可以对指定类的指定 volatile int 字段进行原子更新
         */
        private static final AtomicIntegerFieldUpdater<WeightLoadBalancer.ReadyPicker> indexUpdater =
            AtomicIntegerFieldUpdater.newUpdater(WeightLoadBalancer.ReadyPicker.class, "index");

        /**
         * 子通道列表
         */
        private final List<Subchannel> list;

        @Nullable
        private final WeightLoadBalancer.StickinessState stickinessState;

        @SuppressWarnings("unused")
        private volatile int index;

        private int totalWeight = 0;

        private int maxWeight = 1;

        ReadyPicker(List<Subchannel> list, int startIndex,
            @Nullable WeightLoadBalancer.StickinessState stickinessState) {
            Preconditions.checkArgument(!list.isEmpty(), "empty list");
            this.list = list;
            this.stickinessState = stickinessState;
            this.index = startIndex - 1;
            int newTotalWeight = 0;
            int weightChannelNum = 0;
            int maxWeight = 0;
            for (Subchannel subchannel : list) {
                Integer weight = subchannel.getAttributes().get(WEIGHT_KEY);
                if (weight != null) {
                    if (weight > 0) {
                        newTotalWeight = newTotalWeight + weight;
                    }
                    weightChannelNum = weightChannelNum + 1;
                    if (weight > maxWeight) {
                        maxWeight = weight;
                    }
                }
            }
            int num = list.size() - weightChannelNum;
            if (maxWeight == 0) {
                maxWeight = this.maxWeight;
            }
            if (num > 0) {
                newTotalWeight = newTotalWeight + num * maxWeight;
            }
            this.totalWeight = newTotalWeight;
            this.maxWeight = maxWeight;
        }

        /**
         * 选择子通道
         *
         * @param args
         * @return
         */
        @Override
        public PickResult pickSubchannel(PickSubchannelArgs args) {
            Subchannel subchannel = null;
            if (stickinessState != null) {
                String stickinessValue = args.getHeaders().get(stickinessState.key);
                if (stickinessValue != null) {
                    subchannel = stickinessState.getSubchannel(stickinessValue);
                    if (subchannel == null || !WeightLoadBalancer.isReady(subchannel)) {
                        subchannel = stickinessState.maybeRegister(stickinessValue, nextSubchannel());
                    }
                }
            }

            return PickResult.withSubchannel(subchannel != null ? subchannel : nextSubchannel());
        }

        /**
         * 下一个子通道,主要均衡逻辑
         *
         * @return
         */
        private Subchannel nextSubchannel() {
            int currentWeight = 0;
            for (Subchannel subchannel : list) {
                Integer weight = subchannel.getAttributes().get(WEIGHT_KEY);
                if (weight == null) {
                    weight = maxWeight;
                }
                currentWeight = currentWeight + weight;
                int seed = MathEx.rand(1, totalWeight);
                if (seed <= currentWeight) {
                    return subchannel;
                }
            }
            throw new RuntimeException("权重计算异常");
//            int size = list.size();
//            int i = indexUpdater.incrementAndGet(this);
//            if (i >= size) {
//                int oldi = i;
//                i %= size;
//                indexUpdater.compareAndSet(this, oldi, i);
//            }
//            return list.get(i);
        }

        @VisibleForTesting
        List<Subchannel> getList() {
            return list;
        }

        @Override
        boolean isEquivalentTo(WeightLoadBalancer.WeightPicker picker) {

            if (!(picker instanceof WeightLoadBalancer.ReadyPicker)) {
                return false;
            }

            WeightLoadBalancer.ReadyPicker other = (WeightLoadBalancer.ReadyPicker) picker;
            // the lists cannot contain duplicate subchannels
            return other == this || (stickinessState == other.stickinessState
                && list.size() == other.list.size()
                && new HashSet<Subchannel>(list).containsAll(other.list));

        }

    }

    /**
     * 绑定上下文状态
     * Holds stickiness related states: The stickiness key, a registry mapping stickiness values to
     * the associated Subchannel Ref, and a map from Subchannel to Subchannel Ref.
     */
    @VisibleForTesting
    static final class StickinessState {

        static final int MAX_ENTRIES = 1000;

        final Metadata.Key<String> key;

        final ConcurrentMap<String, Ref<Subchannel>> stickinessMap =
            new ConcurrentHashMap<String, Ref<Subchannel>>();

        final Queue<String> evictionQueue = new ConcurrentLinkedQueue<String>();

        /**
         * 设置客户端请求时附带的属性
         *
         * @param stickinessKey
         */
        StickinessState(@Nonnull String stickinessKey) {
            this.key = Metadata.Key.of(stickinessKey, Metadata.ASCII_STRING_MARSHALLER);
        }

        /**
         * Returns the subchannel associated to the stickiness value if available in both the
         * registry and the round robin list, otherwise associates the given subchannel with the
         * stickiness key in the registry and returns the given subchannel.
         */
        @Nonnull
        Subchannel maybeRegister(
            String stickinessValue, @Nonnull Subchannel subchannel) {
            final Ref<Subchannel> newSubchannelRef = subchannel.getAttributes().get(STICKY_REF);
            while (true) {
                Ref<Subchannel> existingSubchannelRef =
                    stickinessMap.putIfAbsent(stickinessValue, newSubchannelRef);
                if (existingSubchannelRef == null) {
                    // new entry
                    addToEvictionQueue(stickinessValue);
                    return subchannel;
                } else {
                    // existing entry
                    Subchannel existingSubchannel = existingSubchannelRef.value;
                    if (existingSubchannel != null && isReady(existingSubchannel)) {
                        return existingSubchannel;
                    }
                }
                // existingSubchannelRef is not null but no longer valid, replace it
                if (stickinessMap.replace(stickinessValue, existingSubchannelRef, newSubchannelRef)) {
                    return subchannel;
                }
                // another thread concurrently removed or updated the entry, try again
            }
        }

        private void addToEvictionQueue(String value) {
            String oldValue;
            while (stickinessMap.size() >= MAX_ENTRIES && (oldValue = evictionQueue.poll()) != null) {
                stickinessMap.remove(oldValue);
            }
            evictionQueue.add(value);
        }

        /**
         * 从通道属性中移除上下文
         * Unregister the subchannel from StickinessState.
         */
        void remove(Subchannel subchannel) {
            subchannel.getAttributes().get(STICKY_REF).value = null;
        }

        /**
         * Gets the subchannel associated with the stickiness value if there is.
         */
        @Nullable
        Subchannel getSubchannel(String stickinessValue) {
            Ref<Subchannel> subchannelRef = stickinessMap.get(stickinessValue);
            if (subchannelRef != null) {
                return subchannelRef.value;
            }
            return null;
        }
    }

}
