package com.cn.lp.domain;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.cn.lp.GrpcClientSupport;
import com.cn.lp.common.rpc.TestGrpc;
import com.cn.lp.common.rpc.model.HelloReplyMO;
import com.cn.lp.common.rpc.model.HelloRequestMO;
import io.swagger.annotations.ApiOperation;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by qirong on 2019/4/25.
 */

@RestController
public class TestController implements GrpcClientSupport {

    @GrpcClient("lp-grpc-server")
    private TestGrpc.TestBlockingStub testBlockingStub;

    @GetMapping("/test")
    @ApiOperation(value = "测试Grpc调用", notes = "测试Grpc调用")
    public String test() {
        HelloRequestMO requestMO = HelloRequestMO.newBuilder()
            .setName("名字，名字!")
            .build();
        String message = tryCallRPC(testBlockingStub::sayHello, requestMO)
            .map(HelloReplyMO::getMessage)
            .get();
        return message;
    }

    @GetMapping("/testResource")
    @ApiOperation(value = "测试Grpc调用", notes = "测试Grpc调用")
    @SentinelResource(value = "resource", blockHandler = "handleException", blockHandlerClass = {ExceptionUtil.class})
    public String testResource() {
        HelloRequestMO requestMO = HelloRequestMO.newBuilder()
            .setName("名字，名字!")
            .build();
        String message = tryCallRPC(testBlockingStub::sayHello, requestMO)
            .map(HelloReplyMO::getMessage)
            .get();
        return message;
    }

}
