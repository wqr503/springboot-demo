package com.cn.lp.test.domain;

import com.cn.lp.annotations.NeedLogin;
import com.cn.lp.test.domain.role.Account;
import com.cn.lp.test.domain.role.AccountManager;
import com.cn.lp.test.domain.role.AccountMeta;
import com.cn.lp.test.domain.role.SysPermission;
import com.cn.lp.test.domain.utils.CodingUtils;
import com.cn.lp.utils.JwtUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by qirong on 2019/5/31.
 */
@RestController
@ApiOperation(value = "测试接口")
public class TestController {

    @Autowired
    private AccountManager accountManager;

    @Autowired
    private AuthConfig authConfig;

    @GetMapping("test")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "header", dataType = "String", name = "lp_token", value = "令牌", required = true)
    })
    @NeedLogin
    @ApiOperation(value = "测试服务", notes = "测试服务")
    public String test(HttpServletResponse response) {
        return "success";
    }

    @GetMapping("testAuth")
    @ApiOperation(value = "测试登录服务", notes = "测试登录服务")
    public String testAuth() {
        Account account = accountManager.createAccount();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(AccountMeta.UID_PARAM_NAME, account.getUid().toString());
        String token = JwtUtils.sign(authConfig.getSecretKey(), authConfig.getTokenRefreshSpace(), paramMap);
        return CodingUtils.encryptAES(token, authConfig.getTokenSecretKey());
    }

    @NeedLogin(SysPermission.TEST_PERMISSION_1)
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "header", dataType = "String", name = "lp_token", value = "令牌", required = true)
    })
    @GetMapping("testPermission")
    @ApiOperation(value = "测试权限服务", notes = "测试权限服务")
    public String testPermission() {
        return "success";
    }

    @NeedLogin({SysPermission.TEST_PERMISSION_1, SysPermission.TEST_PERMISSION_2})
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "header", dataType = "String", name = "lp_token", value = "令牌", required = true)
    })
    @GetMapping("testNoPermission")
    @ApiOperation(value = "测试无权限服务", notes = "测试无权限服务")
    public String testNoPermission() {
        return "success";
    }

    @NeedLogin
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "header", dataType = "String", name = "lp_token", value = "令牌", required = true)
    })
    @GetMapping("testLogOut")
    @ApiOperation(value = "测试退出", notes = "测试退出")
    public String testLogOut(HttpServletRequest request) {
        request.setAttribute(CommonWebAuthInterceptor.RENEWAL_TOKEN_REQUEST_ATT_NAME, false);
        return "success";
    }

}
