package com.cn.lp;

import com.cn.lp.common.rpc.model.HelloReplyMO;
import com.google.protobuf.Message;
import io.grpc.stub.StreamObserver;
import org.springframework.util.Assert;

/**
 * Grpc服务支持
 */
public interface GrpcServiceSupport {

    /**
     * 包装统一返回
     * @param request
     * @param observer
     * @param action
     * @param <TRequest>
     */
    default <TRequest extends Message>
    void wrap(TRequest request, StreamObserver<HelloReplyMO> observer, Func1Void<TRequest> action) {
        Assert.notNull(action, "action不能为null.");
        try {
            action.call(request);
            var result = HelloReplyMO.newBuilder()
                .setMessage("success")
                .build();
            observer.onNext(result);
            observer.onCompleted();
        } catch (Exception e) {
            GrpcUtils.onError(observer, e);
        }
    }

    default <TRequest extends Message, TResponse extends Message>
    void wrap(TRequest request, StreamObserver<TResponse> observer, Func1<TRequest, TResponse> action) {
        Assert.notNull(action, "action不能为null.");
        try {
            TResponse response = action.call(request);
            observer.onNext(response);
            observer.onCompleted();
        } catch (Exception e) {
            GrpcUtils.onError(observer, e);
        }
    }

}
