package com.cn.lp.domain;

import com.cn.lp.config.ShiroConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * shiro 配置类
 * Created by qirong on 2018/10/10.
 */
@Component
@Configuration
public class TestShiroConfig implements ShiroConfig {

    /**
     * 校验等级
     */
    private VerificationLevel verificationLevel;

    /**
     * 令牌有效时间
     */
    @Value("${shiro.login.token.expireTime:300000}")
    private int tokenExpireTime;

    /**
     * 密码有效时间
     */
    @Value("${shiro.login.password.expireTime:300000}")
    private int passwordExpireTime;

    /**
     * 未登录地址
     */
    @Value("${shiro.login.unloginUrl:/unauth}")
    private String unloginUrl;

    /**
     * 未登录地址
     */
    @Value("${shiro.login.notRoleUrl:/notrole}")
    private String notRoleUrl;


    @Value("${login.verification.level:0}")
    public TestShiroConfig setVerificationLevel(int verificationLevel) {
        this.verificationLevel = VerificationLevel.getLevelByID(verificationLevel);
        return this;
    }

    @Override
    public int getTokenExpireTime() {
        return tokenExpireTime;
    }

    @Override
    public int getPasswordExpireTime() {
        return passwordExpireTime;
    }

    public VerificationLevel getVerificationLevel() {
        return verificationLevel;
    }

    public String getUnloginUrl() {
        return unloginUrl;
    }

    @Override
    public String getNotRoleUrl() {
        return notRoleUrl;
    }

}
