package com.cn.lp.domain;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

/**
 * 对于无状态的TOKEN 类型不创建session
 * Created by qirong on 2019/6/3.
 */
public class AgileSubjectFactory extends DefaultWebSubjectFactory {

    @Override
    public Subject createSubject(SubjectContext context) {
        AuthenticationToken token = context.getAuthenticationToken();
        if((token instanceof JWTToken)){
            // 当token为HmacToken时， 不创建 session
            context.setSessionCreationEnabled(false);
        }
        return super.createSubject(context);
    }
}