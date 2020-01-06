package com.cn.lp.domain;

import com.cn.lp.SpelExcetor;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by on 2019/7/22.
 */
@RestController
@ApiOperation("测试")
public class TestController {

    @Autowired
    private SpelExcetor spelExcetor;

    @ApiOperation(value = "测试在spring ioc环境下")
    @PostMapping(path = "/test")
    public String testInSpringContext(@RequestParam String spel) {
        List<Integer> numList = new ArrayList<>();
        numList.add(1);
        numList.add(2);
        numList.add(3);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("test", new Test());
        dataMap.put("testList", numList);
        Object result = spelExcetor.doneInSpringContext(dataMap, spel);
        return result.toString();
    }

    @ApiOperation(value = "测试不在spring ioc环境下")
    @PostMapping(path = "/test1")
    public String testNotInSpringContext(@RequestParam String spel) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("test", new Test());
        return SpelExcetor.done(dataMap, spel).toString();
    }

    public String test() {
        return "success";
    }

}
