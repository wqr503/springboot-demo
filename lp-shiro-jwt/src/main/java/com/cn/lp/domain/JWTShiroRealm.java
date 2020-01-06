package com.cn.lp.domain;

import com.cn.lp.role.AccountInfo;
import com.cn.lp.role.AccountInfoService;
import com.cn.lp.utils.JwtUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;


/**
 * 自定义身份认证
 * 基于HMAC（ 散列消息认证码）的控制域
 */

public abstract class JWTShiroRealm extends AuthorizingRealm {

    protected abstract AccountInfoService getAccountInfoService();

    public JWTShiroRealm() {

    }

    /**
     * 认证信息.(身份验证) : Authentication 是用来验证用户身份
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        JWTToken jwtToken = (JWTToken) authcToken;
        String token = jwtToken.getToken();
        AccountInfo accountInfo = getAccountInfoService().getAccountInfo(JwtUtils.getUid(token));
        if (accountInfo == null) {
            throw new AuthenticationException("token过期，请重新登录");
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(accountInfo.getUid(), accountInfo.getCredentialsSalt(), "jwtRealm");
        return authenticationInfo;
    }

    /**
     * 在JWT Realm里面，因为没有存储角色信息，所以直接返回空就可以了：
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }
}
