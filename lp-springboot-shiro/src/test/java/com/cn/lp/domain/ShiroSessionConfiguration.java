package com.cn.lp.domain;


import com.cn.lp.domain.cache.EhCacheClient;
import com.cn.lp.domain.realm.NormalCustomRealm;
import com.cn.lp.shiro.config.ShiroConfig;
import com.cn.lp.shiro.config.ShiroFilterChainConfig;
import com.cn.lp.shiro.context.*;
import com.cn.lp.shiro.domain.CustomExceptionHandler;
import com.cn.lp.shiro.domain.CustomRealm;
import com.cn.lp.shiro.filter.AnyRolesAuthorizationFilter;
import com.cn.lp.shiro.filter.TokenRefreshFilter;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Created by qirong on 2018/12/5.
 */
@Configuration
public class ShiroSessionConfiguration extends InitBean {

    @Autowired
    protected ShiroConfig shiroConfig;

    @Autowired
    protected ShiroFilterChainConfig shiroFilterChainConfig;

    @Autowired
    protected EhCacheCacheManager ehCacheCacheManager;

    @Bean(name = "TokenRefreshFilter")
    @ConditionalOnMissingBean
    public TokenRefreshFilter tokenRefreshFilter(ShiroCacheClient shiroCacheClient) {
        TokenRefreshFilter tokenRefreshFilter = new TokenRefreshFilter();
        tokenRefreshFilter.setCacheClient(shiroCacheClient);
        tokenRefreshFilter.setShiroConfig(shiroConfig);
        return tokenRefreshFilter;
    }

    /**
     * 解决No SecurityManager accessible to the calling code
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean delegatingFilterProxy() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("shiroFilter");
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }


    @Bean(name = "shiroFilter")
    @ConditionalOnMissingBean
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager, ShiroDefinitionHolderManager shiroDefinitionHolderManager, AnyRolesAuthorizationFilter anyRolesAuthorizationFilter) {
        return this.createShirFilter(securityManager, shiroDefinitionHolderManager, shiroConfig, shiroFilterChainConfig, anyRolesAuthorizationFilter);
    }


    @Bean(name = "anyRolesAuthorizationFilter")
    @ConditionalOnMissingBean
    public AnyRolesAuthorizationFilter anyRolesAuthorizationFilter() {
        return new AnyRolesAuthorizationFilter();
    }

    @Bean(name = "CustomRealm")
    @ConditionalOnMissingBean
    public CustomRealm customShiroRealm() {
        CustomRealm customRealm = new NormalCustomRealm();
        return this.createCustomRealm(customRealm, createHashedCredentialsMatcher());
    }

    /**
     * 开启shiro aop注解支持.
     * 使用代理方式;所以需要开启代码支持;
     *
     * @param securityManager
     * @return
     */
    @Bean(name = "AuthorizationAttributeSourceAdvisor")
    @ConditionalOnMissingBean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 注册全局异常处理
     *
     * @return
     */
    @Bean(name = "HandlerExceptionResolver")
    @ConditionalOnMissingBean
    public HandlerExceptionResolver handlerExceptionResolver() {
        return new CustomExceptionHandler();
    }

    @Bean(name = "SecurityManager")
    @ConditionalOnMissingBean
    public SecurityManager securityManager(CustomRealm customRealm, ShiroSessionManager sessionManager,
        ShiroCacheManager cacheManager, RememberMeManager rememberMeManager) {
        return this.createSecurityManager(customRealm, sessionManager, cacheManager, rememberMeManager);
    }

    @Bean(name = "ShiroCacheManager")
    @ConditionalOnMissingBean
    public ShiroCacheManager cacheManager(ShiroCacheClient cacheClient) {
        return this.createShiroCacheManager(cacheClient);
    }

    @Bean(name = "ShiroSessionManager")
    @ConditionalOnMissingBean
    public ShiroSessionManager redisSessionManager(ShiroCacheClient cacheClient, ShiroSessionDAO sessionDAO) {
        return this.createShiroSessionManager(cacheClient, sessionDAO);
    }

    /**
     * RedisSessionDAO shiro sessionDao层的实现 通过redis
     * <p>
     * 使用的是shiro-redis开源插件
     */
    @Bean(name = "ShiroSessionDAO")
    @ConditionalOnMissingBean
    public ShiroSessionDAO shiroSessionDAO(ShiroCacheClient cacheClient) {
        return this.createShiroSessionDAO(cacheClient, shiroConfig);
    }

    @Bean(name = "ehCacheClient")
    @ConditionalOnMissingBean
    public EhCacheClient ehCacheClient() {
        return new EhCacheClient(ehCacheCacheManager);
    }

    @Bean(name = "RememberMeManager")
    @ConditionalOnMissingBean
    public RememberMeManager rememberMeManager() {
        return this.createRememberMeManager(shiroConfig);
    }

}
