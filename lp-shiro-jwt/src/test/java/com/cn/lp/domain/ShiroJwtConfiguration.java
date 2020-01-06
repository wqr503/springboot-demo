package com.cn.lp.domain;


import com.cn.lp.config.ShiroConfig;
import com.cn.lp.config.ShiroFilterChainConfig;
import com.cn.lp.context.CustomExceptionHandler;
import com.cn.lp.context.InitBean;
import com.cn.lp.context.ShiroDefinitionHolderManager;
import com.cn.lp.domain.realm.NormalCustomRealm;
import com.cn.lp.domain.realm.NormalJWTRealm;
import com.cn.lp.filter.AnyRolesAuthorizationFilter;
import com.cn.lp.filter.JwtAuthFilter;
import com.cn.lp.filter.TokenRefreshFilter;
import com.cn.lp.role.AccountInfoService;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Created by qirong on 2018/12/5.
 */
@Configuration
public class ShiroJwtConfiguration extends InitBean {

    @Autowired
    protected ShiroConfig shiroConfig;

    @Autowired
    protected ShiroFilterChainConfig shiroFilterChainConfig;

    @Bean(name = "TokenRefreshFilter")
    @ConditionalOnMissingBean
    public TokenRefreshFilter tokenRefreshFilter(AccountInfoService accountInfoService) {
        TokenRefreshFilter tokenRefreshFilter = new TokenRefreshFilter();
        tokenRefreshFilter.setAccountInfoService(accountInfoService);
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
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager, ShiroDefinitionHolderManager shiroDefinitionHolderManager,
        JwtAuthFilter jwtAuthFilter, AnyRolesAuthorizationFilter anyRolesAuthorizationFilter) {
        return this.createShirFilter(securityManager, shiroDefinitionHolderManager, shiroConfig, shiroFilterChainConfig,
            jwtAuthFilter, anyRolesAuthorizationFilter
        );
    }

    @Bean(name = "jwtAuthFilter")
    @ConditionalOnMissingBean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter();
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
        return this.createCustomRealm(customRealm, createHashedCredentialsMatcher(shiroConfig));
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
    public SecurityManager securityManager(JWTShiroRealm jwtShiroRealm, CustomRealm customRealm) {
        return this.createSecurityManager(jwtShiroRealm, customRealm);
    }

    @Bean
    @ConditionalOnMissingBean
    public SessionStorageEvaluator sessionStorageEvaluator() {
        return this.createSessionStorageEvaluator();
    }

    @Bean(name = "JWTShiroRealm")
    @ConditionalOnMissingBean
    public JWTShiroRealm jwtShiroRealm() {
        JWTShiroRealm jwtShiroRealm = new NormalJWTRealm();
        return this.createJWTShiroRealm(jwtShiroRealm);
    }

}
