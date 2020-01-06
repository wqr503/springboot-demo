package com.cn.lp.shiro.domain;

import com.cn.lp.shiro.context.VerificationLevel;
import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;

import java.time.format.DateTimeFormatter;

/**
 * 登录消息TOKEN
 * Created by xiaoqing on 2018/10/7.
 */
public class LoginMessageToken implements HostAuthenticationToken, RememberMeAuthenticationToken {

    public final static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 校验等级
     */
    private VerificationLevel verificationLevel;

    /**
     * 登录时间
     */
    private long loginTime;

    /**
     * 账户名
     */
    private String username;

    /**
     * 密码
     */
    private char[] password;

    /**
     * 是否记住我
     */
    private boolean rememberMe;

    /**
     * IP地址
     */
    private String host;

    public LoginMessageToken(String username, char[] password, String host, VerificationLevel level, long loginTime, boolean rememberMe) {
        this.username = username;
        this.host = host;
        this.verificationLevel = level;
        this.loginTime = loginTime;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    public LoginMessageToken(String username, char[] password, String host, VerificationLevel level, long loginTime) {
        this(username, password, host, level, loginTime, false);
    }

    public LoginMessageToken(String username, String password, String host, VerificationLevel level, long loginTime) {
        this(username, password != null ? password.toCharArray() : null, host, level, loginTime, false);
    }

    public LoginMessageToken(String username, String password, String host, VerificationLevel level, long loginTime, boolean rememberMe) {
        this(username, password != null ? password.toCharArray() : null, host, level, loginTime, rememberMe);
    }

    public VerificationLevel getVerificationLevel() {
        return verificationLevel;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public String getUsername() {
        return this.username;
    }

    public char[] getPassword() {
        return this.password;
    }

    @Override
    public Object getPrincipal() {
        return this.getUsername();
    }

    @Override
    public Object getCredentials() {
        return this.getPassword();
    }

    @Override
    public String getHost() {
        return this.host;
    }

    protected void setHost(String host) {
        this.host = host;
    }

    @Override
    public boolean isRememberMe() {
        return this.rememberMe;
    }

    protected void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public LoginMessageToken setVerificationLevel(VerificationLevel verificationLevel) {
        this.verificationLevel = verificationLevel;
        return this;
    }

    protected LoginMessageToken setLoginTime(long loginTime) {
        this.loginTime = loginTime;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append(" - ");
        sb.append(this.username);
        sb.append(", rememberMe=").append(this.rememberMe);
        sb.append(", loginTime=").append(this.loginTime);
        sb.append(", verificationGrade=").append(this.verificationLevel);
        if (this.host != null) {
            sb.append(" (").append(this.host).append(")");
        }
        return sb.toString();
    }

}
