package com.cn.lp.filter;


import com.cn.lp.config.ShiroConfig;
import com.cn.lp.role.AccountInfo;
import com.cn.lp.role.AccountInfoService;
import com.cn.lp.utils.AccountAide;
import com.cn.lp.utils.JwtUtils;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * 令牌刷新过滤器
 * Created by xiaoqing on 2018/12/23.
 */
public class TokenRefreshFilter implements Filter {

    private ShiroConfig shiroConfig;

    private AccountInfoService accountInfoService;

    private static Logger LOGGER = LoggerFactory.getLogger(TokenRefreshFilter.class);

    public TokenRefreshFilter() {

    }

    public TokenRefreshFilter setShiroConfig(ShiroConfig shiroConfig) {
        this.shiroConfig = shiroConfig;
        return this;
    }

    public TokenRefreshFilter setAccountInfoService(AccountInfoService accountInfoService) {
        this.accountInfoService = accountInfoService;
        return this;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletResponse.setCharacterEncoding("utf-8");
        Object token = servletRequest.getAttribute(AccountAide.JWT_TOKEN_NAME);
        if (token != null) {
            refreshJwtToken(WebUtils.toHttp(servletRequest), WebUtils.toHttp(servletResponse), token.toString());
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    protected void refreshJwtToken(HttpServletRequest request, HttpServletResponse response, String jwtToken) {
        try {
            //ZoneId.systemDefault() 时区
            //签发时间
            LocalDateTime issueTime = LocalDateTime.ofInstant(JwtUtils.getIssuedAt(jwtToken).toInstant(), ZoneId.systemDefault());
            if (this.getRefreshTime(issueTime).isBefore(LocalDateTime.now())) {
                Long uid = JwtUtils.getUid(jwtToken);
                AccountInfo accountInfo = accountInfoService.getAccountInfo(uid);
                AccountAide.bindJwtToken(accountInfo, request, response, shiroConfig.getTokenExpireTime());
            }
        } catch (Exception e) {
            LOGGER.error("刷新JWT令牌异常: ", e);
        }
    }

    /**
     * 获取3分2的有效时间作为刷新时间
     * @param issueTime
     * @return
     */
    private LocalDateTime getRefreshTime(LocalDateTime issueTime) {
        return issueTime.plus((long) (shiroConfig.getTokenExpireTime() * (2.0 / 3.0)), ChronoUnit.MILLIS);
    }

    @Override
    public void destroy() {

    }

}
