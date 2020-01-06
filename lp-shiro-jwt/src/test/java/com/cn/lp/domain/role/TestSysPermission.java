package com.cn.lp.domain.role;


import com.cn.lp.role.SysPermission;

/**
 * Created by qirong on 2018/9/19.
 */
public class TestSysPermission implements SysPermission {

    private String permission;

    public TestSysPermission(String permission) {
        this.permission = permission;
    }

    @Override
    public String getPermission() {
        return permission;
    }

}
