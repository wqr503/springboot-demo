package com.cn.lp.domain;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by qirong on 2019/4/25.
 */

@ApiOperation(value = "测试接口")
@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("test")
    @ApiOperation(value = "测试服务", notes = "测试服务")
    public String test() {
        return "success";
    }

    @GetMapping("testCache")
    @ApiOperation(value = "测试缓存", notes = "测试缓存")
    public String testCache() {
        List<Integer> dataList = testService.getDataList();
        StringBuilder sb = new StringBuilder();
        for(Integer data : dataList) {
            sb.append(data);
        }
        return sb.toString();
    }

    @GetMapping("testRedirect")
    @ApiOperation(value = "测试重定向服务", notes = "测试重定向服务")
    public void testRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String realServerName = request.getHeader("realmName");
        String path = request.getContextPath();
        String basePath = "";
        if ((!StringUtils.isEmpty(realServerName))) {
            basePath = "http://" + realServerName + path;
        }
        response.sendRedirect(basePath + "/qa/test");
    }
}
