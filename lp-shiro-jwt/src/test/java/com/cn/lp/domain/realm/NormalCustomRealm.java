package com.cn.lp.domain.realm;

import com.cn.lp.config.ShiroConfig;
import com.cn.lp.domain.CustomRealm;
import com.cn.lp.role.AccountInfoService;
import com.cn.lp.role.SysPermissionService;
import com.cn.lp.role.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by qirong on 2019/4/8.
 */
public class NormalCustomRealm extends CustomRealm {

    @Autowired
    private AccountInfoService accountInfoService;

    @Autowired
    private SysPermissionService sysPermissionService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private ShiroConfig shiroConfig;

    @Override
    protected AccountInfoService getAccountInfoService() {
        return accountInfoService;
    }

    @Override
    protected SysRoleService getSysRoleService() {
        return sysRoleService;
    }

    @Override
    protected SysPermissionService getSysPermissionService() {
        return sysPermissionService;
    }

    @Override
    protected ShiroConfig getShiroConfig() {
        return shiroConfig;
    }
}
