package com.cn.lp.domain;

import com.cn.lp.shiro.config.ShiroConfig;
import com.cn.lp.shiro.context.VerificationLevel;
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
     * session过期时间
     */
    @Value("${shiro.session.outTime:300000}")
    private long sessionTimeOut;

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

    /**
     * rememberMe 密钥
     */
    @Value("${shiro.login.rememberMeSecretKey:3AvVhmFLUs0KTA3Kprsdag==}")
    private String rememberMeSecretKey;

    /**
     * rememberMe 有效时长
     */
    @Value("${shiro.login.rememberMeExpireTime:259200}")
    private long rememberMeExpireTime;

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

    @Override
    public long getSessionTimeOut() {
        return sessionTimeOut;
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

    @Override
    public String getRememberMeSecretKey() {
        return rememberMeSecretKey;
    }

    @Override
    public long getRememberMeExpireTime() {
        return rememberMeExpireTime;
    }

}
