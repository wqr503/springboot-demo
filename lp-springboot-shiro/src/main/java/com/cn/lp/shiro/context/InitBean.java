package com.cn.lp.shiro.context;

import com.cn.lp.shiro.config.ShiroConfig;
import com.cn.lp.shiro.config.ShiroFilterChainConfig;
import com.cn.lp.shiro.domain.CustomRealm;
import com.cn.lp.shiro.domain.LoginMessageToken;
import com.cn.lp.shiro.filter.AnyRolesAuthorizationFilter;
import com.cn.lp.shiro.utils.AccountAide;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;

import javax.servlet.Filter;
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
        ShiroConfig shiroConfig, ShiroFilterChainConfig shiroFilterChainConfig,
        AnyRolesAuthorizationFilter anyRolesAuthorizationFilter) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filterMap = shiroFilterFactoryBean.getFilters();
        filterMap.put("anyRole", anyRolesAuthorizationFilter);
        shiroFilterFactoryBean.setFilters(filterMap);
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
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
     * @return
     */
    public HashedCredentialsMatcher createHashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        //散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(1);
        // 这一行决定hex(true)还是base64(false)
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return hashedCredentialsMatcher;
    }

    /**
     * 构建缓存管理者
     *
     * @param cacheClient
     * @return
     */
    public ShiroCacheManager createShiroCacheManager(ShiroCacheClient cacheClient) {
        ShiroCacheManager cacheManager = new ShiroCacheManager();
        cacheManager.setCacheClient(cacheClient);
        return cacheManager;
    }

    /**
     * 构建Session管理者
     *
     * @param cacheClient
     * @param sessionDAO
     * @return
     */
    public ShiroSessionManager createShiroSessionManager(ShiroCacheClient cacheClient, ShiroSessionDAO sessionDAO) {
        ShiroSessionManager shiroSessionManager = new ShiroSessionManager();
        shiroSessionManager.setSessionIdUrlRewritingEnabled(false);
        shiroSessionManager.setSessionDAO(sessionDAO);
        shiroSessionManager.setCacheClient(cacheClient);
        return shiroSessionManager;
    }

    /**
     * 构建SessionDAO
     *
     * @param cacheClient
     * @param shiroConfig
     * @return
     */
    public ShiroSessionDAO createShiroSessionDAO(ShiroCacheClient cacheClient, ShiroConfig shiroConfig) {
        ShiroSessionDAO shiroSessionDAO = new ShiroSessionDAO();
        shiroSessionDAO.setShiroConfig(shiroConfig);
        shiroSessionDAO.setCacheClient(cacheClient);
        return shiroSessionDAO;
    }

    /**
     * 构建RememberMe的cookie管理器;
     *
     * @return
     */
    public CookieRememberMeManager createRememberMeManager(ShiroConfig shiroConfig) {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        //rememberme cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度（128 256 512 位），通过以下代码可以获取
        //对称加解密钥
        byte[] cipherKey = Base64.decode(shiroConfig.getRememberMeSecretKey());
        cookieRememberMeManager.setCipherKey(cipherKey);
        cookieRememberMeManager.setCookie(createRememberMeCookie(shiroConfig));
        return cookieRememberMeManager;
    }

    /**
     * 构建RememberMeCookie
     *
     * @param shiroConfig
     * @return
     */
    private SimpleCookie createRememberMeCookie(ShiroConfig shiroConfig) {
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie(AccountAide.REMEMBER_ME_NAME);
        //如果httyOnly设置为true，则客户端不会暴露给客户端脚本代码，使用HttpOnly cookie有助于减少某些类型的跨站点脚本攻击；
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge((int) (shiroConfig.getRememberMeExpireTime() / 1000));
        return simpleCookie;
    }

    private DefaultWebSecurityManager doCreateSecurityManager(ShiroSessionManager sessionManager,
        ShiroCacheManager cacheManager, RememberMeManager rememberMeManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSessionManager(sessionManager);
        if (cacheManager != null) {
            // 自定义缓存实现 使用redis
            securityManager.setCacheManager(cacheManager);
        }
        if (rememberMeManager != null) {
            securityManager.setRememberMeManager(rememberMeManager);
        }
        return securityManager;
    }

    /**
     * 构建SecurityManage
     *
     * @param customRealm
     * @param sessionManager
     * @param cacheManager
     * @param rememberMeManager
     * @return
     */
    public SecurityManager createSecurityManager(CustomRealm customRealm, ShiroSessionManager sessionManager,
        ShiroCacheManager cacheManager, RememberMeManager rememberMeManager) {
        DefaultWebSecurityManager securityManager = doCreateSecurityManager(sessionManager, cacheManager, rememberMeManager);
        securityManager.setRealm(customRealm);
        return securityManager;
    }

}
