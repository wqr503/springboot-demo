package com.cn.lp.domain;

import com.alibaba.fastjson.JSONObject;
import com.cn.lp.shiro.annotation.ShiroDefinition;
import com.cn.lp.shiro.config.ShiroConfig;
import com.cn.lp.shiro.context.ShiroCacheClient;
import com.cn.lp.shiro.context.VerificationLevel;
import com.cn.lp.shiro.domain.LoginMessageToken;
import com.cn.lp.shiro.role.AccountInfoService;
import com.cn.lp.shiro.utils.AccountAide;
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
@Api(tags = "测试API")
@RestController
public class TestController {

    @Autowired
    private ShiroCacheClient shiroCacheClient;

    @Autowired
    private ShiroConfig shiroConfig;

    @ApiOperation(value = "测试RememberMe", notes = "测试RememberMe")
    @GetMapping("/test")
    @ShiroDefinition(filter = "user")
    public String testUser() {
        return "success";
    }

    @ApiOperation(value = "测试角色", notes = "测试角色")
    @GetMapping("/test/roles")
    @ShiroDefinition(filter = "authc,roles[admin]")
    public String testRoles() {
        return "success";
    }

    @ApiOperation(value = "测试多角色", notes = "测试多角色")
    @GetMapping("/test/anyRoles")
    @ShiroDefinition(filter = "authc,anyRole[admin,user]")
    public String testAnyRoles() {
        return "success";
    }

    @ApiOperation(value = "测试权限", notes = "测试权限")
    @GetMapping("/test/perms")
    @ShiroDefinition(filter = "authc,perms[read]")
    public String testPerms() {
        return "success";
    }

    @ApiOperation(value = "测试权限和角色", notes = "测试权限和角色")
    @GetMapping("/test/rolesAndPerms")
    @ShiroDefinition(filter = "authc,roles[admin],perms[read]")
    public String testRolesAndPerms() {
        return "success";
    }

    @ApiOperation(value = "测试权限和角色失败情况", notes = "测试权限和角色失败情况")
    @GetMapping("/test/warnRolesAndPerms")
    @ShiroDefinition(filter = "authc,roles[user],perms[user]")
    public String testWarnRolesAndPerms() {
        return "success";
    }

    @ApiOperation(value = "登出", notes = "登出")
    @GetMapping("logout")
    public String testLogout(HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            subject.logout();
            AccountAide.clearToken(request, shiroCacheClient);
            return "success";
        }
        return "false";
    }

    @ApiOperation(value = "登录", notes = "登录")
    @GetMapping("login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        Subject subject = SecurityUtils.getSubject();
        LoginMessageToken token = new LoginMessageToken("admin", "admin", request.getRemoteHost(), VerificationLevel.CIPHER_PASSWORD, System.currentTimeMillis(), true);
        try {
            subject.login(token);
            AccountAide.bindToken(request, response, shiroCacheClient, subject.getSession(), shiroConfig.getSessionTimeOut(), shiroConfig.getTokenExpireTime());
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

