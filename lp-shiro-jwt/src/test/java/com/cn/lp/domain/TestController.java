package com.cn.lp.domain;

import com.alibaba.fastjson.JSONObject;
import com.cn.lp.annotation.ShiroDefinition;
import com.cn.lp.config.ShiroConfig;
import com.cn.lp.role.AccountInfoService;
import com.cn.lp.utils.AccountAide;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by qirong on 2018/9/19.
 */
@ApiOperation(value = "测试API")
@RestController
public class TestController {

    @Autowired
    private ShiroConfig shiroConfig;

    @Autowired
    private AccountInfoService accountInfoService;


    @ApiOperation(value = "测试JWt", notes = "测试JWt")
    @GetMapping("/test/jwt")
    @ShiroDefinition
    public String testJwt() {
        return "success";
    }

    @ApiOperation(value = "测试JWt角色", notes = "测试JWt角色")
    @GetMapping("/test/jwtRole")
    @ShiroDefinition(filter = "authcJwt,anyRole[test]")
    public String testJwtRole() {
        return "success";
    }

    @ApiOperation(value = "测试JWt权限", notes = "测试JWt权限")
    @GetMapping("/test/jwtPerms")
    @ShiroDefinition(filter = "authcJwt,perms[read]")
    public String testJwtPermissive() {
        return "success";
    }

    @ApiOperation(value = "测试JWt权限，包含只需有权限就成功，无需登录", notes = "测试JWt权限，包含只需有权限就成功，无需登录")
    @GetMapping("/test/jwtPermissive")
    @ShiroDefinition(filter = "authcJwt[permissive]")
    public String testJwtAndPermissive() {
        Subject subject = SecurityUtils.getSubject();
        System.out.println("拥有read 权限" + subject.isPermitted("read"));
        System.out.println("拥有write 权限" + subject.isPermitted("write"));
        return "success";
    }

    @ApiOperation(value = "登出", notes = "登出")
    @GetMapping("logout")
    public String testLogout(HttpServletResponse response) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            subject.logout();
            AccountAide.clearToken(response);
            return "success";
        }
        return "false";
    }

    @ApiOperation(value = "登录", notes = "登录")
    @GetMapping("login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        Subject subject = SecurityUtils.getSubject();
        LoginMessageToken token = new LoginMessageToken("admin", "admin", request.getRemoteHost(), VerificationLevel.CIPHER_PASSWORD, System.currentTimeMillis(), false);
        try {
            subject.login(token);
            AccountAide.bindJwtToken(accountInfoService.searchAccountInfo("admin"), request, response, shiroConfig.getTokenExpireTime());
            jsonObject.put("token", subject.getSession().getId());
            jsonObject.put("msg", "登录成功");
        } catch (IncorrectCredentialsException e) {
            e.printStackTrace();
            jsonObject.put("msg", "密码错误");
        } catch (LockedAccountException e) {
            e.printStackTrace();
            jsonObject.put("msg", "登录失败，该用户已被冻结");
        } catch (AuthenticationException e) {
            e.printStackTrace();
            jsonObject.put("msg", "该用户不存在");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    /**
     * 未登录，shiro应重定向到登录界面，此处返回未登录状态信息由前端控制跳转页面
     *
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/unauth")
    @ResponseBody
    public Object unauth() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", "1000000");
        map.put("msg", "未登录");
        return map;
    }

    /**
     * 无权限，shiro应重定向到登录界面，此处返回未登录状态信息由前端控制跳转页面
     *
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/notrole")
    @ResponseBody
    public Object notrole() {
        Subject subject = SecurityUtils.getSubject();
        Map<String, Object> map = new HashMap<>();
        if (subject.isAuthenticated()) {
            map.put("code", "1000000");
            map.put("msg", "没有权限");
        } else {
            map.put("code", "1000000");
            map.put("msg", "未登录");
        }
        return map;
    }

}

