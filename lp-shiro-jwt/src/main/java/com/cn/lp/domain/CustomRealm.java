package com.cn.lp.domain;

import com.cn.lp.config.ShiroConfig;
import com.cn.lp.role.*;
import com.cn.lp.utils.AccountAide;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.security.auth.login.AccountLockedException;
import java.util.List;

/**
 * 默认CustomRealm
 *
 * @author qirong
 * @date 2018/9/25
 */
public abstract class CustomRealm extends AuthorizingRealm {

    protected abstract AccountInfoService getAccountInfoService();

    protected abstract SysRoleService getSysRoleService();

    protected abstract SysPermissionService getSysPermissionService();

    protected abstract ShiroConfig getShiroConfig();

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Long uid = (Long) principals.getPrimaryPrincipal();
        AccountInfo accountInfo = this.getAccountInfoService().getAccountInfo(uid);
        try {
            SysRoleService sysRoleService = this.getSysRoleService();
            if (sysRoleService != null) {
                List<SysRole> roles = sysRoleService.selectRoleByInfo(accountInfo);
                for (SysRole role : roles) {
                    authorizationInfo.addRole(role.getRole());
                }
            }
            SysPermissionService sysPermissionService = this.getSysPermissionService();
            if (sysPermissionService != null) {
                List<SysPermission> sysPermissions = sysPermissionService.selectPermByInfo(accountInfo);
                for (SysPermission perm : sysPermissions) {
                    authorizationInfo.addStringPermission(perm.getPermission());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
        throws AuthenticationException {
        if (token instanceof LoginMessageToken) {
            ShiroConfig shiroConfig = this.getShiroConfig();
            LoginMessageToken messageToken = (LoginMessageToken) token;
            String accountName = messageToken.getUsername();
            AccountInfo accountInfo = this.getAccountInfoService().searchAccountInfo(accountName);
            if (accountInfo == null) {
                return null;
            }
            String password = accountInfo.getPassword();
            if (messageToken.getVerificationLevel() == VerificationLevel.TIME_PASSWORD) {
                if (System.currentTimeMillis() > messageToken.getLoginTime() + shiroConfig.getPasswordExpireTime()) {
                    throw new IncorrectCredentialsException("登录密码超时");
                }
                // 前端发送时要对密码加密由MAD5(密码, loginTime)组成
                password = AccountAide.MD5(password, messageToken.getLoginTime() + "");
            }
            if(accountInfo.getState().isLock()) {
                throw new LockedAccountException();
            }
            password = new SimpleHash("MD5", password, accountInfo.getCredentialsSalt(), 1).toHex();
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                accountInfo.getUid(),
                password,
                ByteSource.Util.bytes(accountInfo.getCredentialsSalt()),
                getName()
            );
            return authenticationInfo;
        }
        return null;
    }

}