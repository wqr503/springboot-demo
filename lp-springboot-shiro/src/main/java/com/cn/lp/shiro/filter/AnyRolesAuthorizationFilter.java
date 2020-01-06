package com.cn.lp.shiro.filter;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 多角色校验过滤器
 */
public class AnyRolesAuthorizationFilter extends RolesAuthorizationFilter {

    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) {
        request.setAttribute("anyRolesAuthFilter.FILTERED", true);
    }

    @Override
    public boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object mappedValue) {
        Boolean afterFiltered = (Boolean) (servletRequest.getAttribute("anyRolesAuthFilter.FILTERED"));
        if (BooleanUtils.isTrue(afterFiltered)) {
            return true;
        }
        Subject subject = getSubject(servletRequest, servletResponse);
        String[] rolesArray = (String[]) mappedValue;
        //没有角色限制，有权限访问
        if (rolesArray == null || rolesArray.length == 0) {
            return true;
        }
        for (String role : rolesArray) {
            //若当前用户是rolesArray中的任何一个，则有权限访问
            if (subject.hasRole(role)) {
                return true;
            }
        }
        return false;
    }

}
