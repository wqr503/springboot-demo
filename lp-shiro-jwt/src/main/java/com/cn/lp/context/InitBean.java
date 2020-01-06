package com.cn.lp.context;

import com.cn.lp.config.ShiroConfig;
import com.cn.lp.config.ShiroFilterChainConfig;
import com.cn.lp.domain.*;
import com.cn.lp.filter.AnyRolesAuthorizationFilter;
import com.cn.lp.filter.JwtAuthFilter;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSessionStorageEvaluator;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 初始化Bean
 *
 * @author qirong
 * @date 2019/4/8
 */
public abstract class InitBean {

    /**
     * 构建ShirFilte
     *
     * @param securityManager
     * @param shiroDefinitionHolderManager
     * @param shiroConfig
     * @param shiroFilterChainConfig
     * @return
     */
    public ShiroFilterFactoryBean createShirFilter(SecurityManager securityManager, ShiroDefinitionHolderManager shiroDefinitionHolderManager,
        ShiroConfig shiroConfig, ShiroFilterChainConfig shiroFilterChainConfig, JwtAuthFilter jwtAuthFilter,
        AnyRolesAuthorizationFilter anyRolesAuthorizationFilter) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filterMap = shiroFilterFactoryBean.getFilters();
        filterMap.put("authcJwt", jwtAuthFilter);
        filterMap.put("anyRole", anyRolesAuthorizationFilter);
        shiroFilterFactoryBean.setFilters(filterMap);
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap();
        for (Map.Entry<String, String> entry : shiroDefinitionHolderManager.getShiroDefinitionMap().entrySet()) {
            filterChainDefinitionMap.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : shiroFilterChainConfig.getFilterChainsMap().entrySet()) {
            filterChainDefinitionMap.put(entry.getKey(), entry.getValue());
        }
        // 设置无权限时跳转的 url;
        shiroFilterFactoryBean.setUnauthorizedUrl(shiroConfig.getNotRoleUrl());
        shiroFilterFactoryBean.setLoginUrl(shiroConfig.getUnloginUrl());
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 构建默认Realm
     *
     * @param customRealm
     * @return
     */
    public CustomRealm createCustomRealm(CustomRealm customRealm, HashedCredentialsMatcher hashedCredentialsMatcher) {
        customRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        customRealm.setAuthenticationTokenClass(LoginMessageToken.class);
        return customRealm;
    }

    /**
     * 构建默认密码解析器
     *
     * @param shiroConfig
     * @return
     */
    public HashedCredentialsMatcher createHashedCredentialsMatcher(ShiroConfig shiroConfig) {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        //散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(1);
        // 这一行决定hex(true)还是base64(false)
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return hashedCredentialsMatcher;
    }

    /**
     * 构建JWTSecurityManage
     *
     * @param customRealm
     * @return
     */
    public SecurityManager createSecurityManager(JWTShiroRealm jwtShiroRealm, CustomRealm customRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealms(Arrays.asList(jwtShiroRealm, customRealm));
        securityManager.setSubjectFactory(new AgileSubjectFactory());
        securityManager.setAuthenticator(this.createJwtAuthenticator(jwtShiroRealm, customRealm));
        return securityManager;
    }

    /**
     * 初始化Authenticator
     */
    private ModularRealmAuthenticator createJwtAuthenticator(JWTShiroRealm jwtShiroRealm, CustomRealm customRealm) {
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        //设置两个Realm，一个用于用户登录验证和访问权限获取；一个用于jwt token的认证
        authenticator.setRealms(Arrays.asList(jwtShiroRealm, customRealm));
        //设置多个realm认证策略，一个成功即跳过其它的
        authenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
        return authenticator;
    }

    /**
     * JWT 下禁用session, 不保存用户登录状态。保证每次请求都重新认证。
     * 需要注意的是，如果用户代码里调用Subject.getSession()还是可以用session，如果要完全禁用，要配合下面的noSessionCreation的Filter来实现
     */
    protected SessionStorageEvaluator createSessionStorageEvaluator() {
        DefaultWebSessionStorageEvaluator sessionStorageEvaluator = new DefaultWebSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        return sessionStorageEvaluator;
    }

    /**
     * 构建JWT Realm
     *
     * @param jwtShiroRealm
     * @return
     */
    public JWTShiroRealm createJWTShiroRealm(JWTShiroRealm jwtShiroRealm) {
        jwtShiroRealm.setCredentialsMatcher(new JWTCredentialsMatcher());
        jwtShiroRealm.setAuthenticationTokenClass(JWTToken.class);
        return jwtShiroRealm;
    }

}
