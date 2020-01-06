package com.cn.lp.shiro.filter;


import com.cn.lp.shiro.config.ShiroConfig;
import com.cn.lp.shiro.context.ShiroCacheClient;
import com.cn.lp.shiro.utils.AccountAide;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.servlet.*;
import java.io.IOException;

/**
 * 令牌刷新过滤器
 * Created by xiaoqing on 2018/12/23.
 */
public class TokenRefreshFilter implements Filter {

    private ShiroCacheClient cacheClient;

    private ShiroConfig shiroConfig;

    private static Logger LOGGER = LoggerFactory.getLogger(TokenRefreshFilter.class);

    public TokenRefreshFilter setCacheClient(ShiroCacheClient cacheClient) {
        this.cacheClient = cacheClient;
        return this;
    }

    public TokenRefreshFilter setShiroConfig(ShiroConfig shiroConfig) {
        this.shiroConfig = shiroConfig;
        return this;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletResponse.setCharacterEncoding("utf-8");
        Object token = servletRequest.getAttribute(AccountAide.SESSION_TOKEN_NAME);
        if (token != null) {
            Assert.notNull(cacheClient, "缓存客户端不能为空");
            refreshSessionToken(servletRequest, servletResponse, token.toString());
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    protected void refreshSessionToken(ServletRequest servletRequest, ServletResponse servletResponse, String token) {
        try {
            String sessionID = cacheClient.get(token);
            if (sessionID != null) {
                Long tokenRefreshTime = cacheClient.get(token + AccountAide.TOKEN_REFRESH_TIME);
                if (tokenRefreshTime != null) {
                    if (System.currentTimeMillis() > tokenRefreshTime) {
                        Subject subject = SecurityUtils.getSubject();
                        AccountAide.bindToken(WebUtils.toHttp(servletRequest), WebUtils.toHttp(servletResponse),
                            cacheClient, subject.getSession(), shiroConfig.getSessionTimeOut(),
                            shiroConfig.getTokenExpireTime()
                        );
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("刷新令牌异常: ", e);
        }
    }

    @Override
    public void destroy() {

    }

}
