package com.cn.lp;

import com.google.protobuf.Message;

import java.util.Optional;

public interface GrpcClientSupport {

    default <TRequest extends Message, TResponse extends Message> TResponse callRPC(Func1<TRequest, TResponse> action, TRequest request) {
        return GrpcUtils.call(action, request);
    }

    default <TRequest extends Message, TResponse extends Message> Optional<TResponse> tryCallRPC(Func1<TRequest, TResponse> action, TRequest request) {
        return GrpcUtils.tryCall(action, request);
    }

}
