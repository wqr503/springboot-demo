package com.cn.lp;

import com.google.protobuf.Message;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.Objects;
import java.util.Optional;

public final class GrpcUtils {

    /**
     * 服务端header的key
     */
    public static final Metadata.Key<String> MSG_KEY = Metadata.Key.of("msg", Metadata.ASCII_STRING_MARSHALLER);

    private GrpcUtils() {

    }

    public static void onError(StreamObserver<?> observer, Throwable t) {
        var m = new Metadata();
        m.put(MSG_KEY, t.getMessage());
        observer.onError(new StatusRuntimeException(Status.UNKNOWN.withCause(t), m));
    }

    public static <TRequest extends Message, TResponse extends Message> TResponse call(Func1<TRequest, TResponse> action, TRequest request) {
        try {
            return action.call(request);
        } catch (StatusRuntimeException e) {
            if (Objects.nonNull(e.getTrailers())) {
                var m = e.getTrailers();
                if (m.containsKey(MSG_KEY)) {
                    String msg = m.get(MSG_KEY);
                    throw new RuntimeException(msg);
                }
            }
            throw new RuntimeException(e);
        }
    }

    public static <TRequest extends Message, TResponse extends Message> Optional<TResponse> tryCall(Func1<TRequest, TResponse> action, TRequest request) {
        try {
            return Optional.ofNullable(action.call(request));
        } catch (StatusRuntimeException e) {
        }
        return Optional.empty();
    }


}
