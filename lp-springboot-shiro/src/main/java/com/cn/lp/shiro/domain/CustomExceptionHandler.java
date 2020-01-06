package com.cn.lp.shiro.domain;

import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author qirong
 * @date 2017/12/11
 * 全局异常处理
 */
public class CustomExceptionHandler implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception ex) {
        httpServletResponse.setStatus(401);
        httpServletResponse.setHeader("Content-type", "charset=UTF-8");
        ModelAndView empty = new ModelAndView();
        PrintWriter pw = null;
        try {
            pw = httpServletResponse.getWriter();
            if (ex instanceof UnauthenticatedException) {
                pw.write("登录异常");
            } else if (ex instanceof UnauthorizedException) {
                pw = httpServletResponse.getWriter();
                pw.write("用户无权限");
            } else {
                ex.printStackTrace();
                pw.write("服务器异常");
            }
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }
        empty.clear();
        return empty;
    }
}
