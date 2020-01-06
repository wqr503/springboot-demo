package com.cn.lp.domain.role;


import com.cn.lp.role.SysRole;

/**
 * Created by qirong on 2018/9/19.
 */
public class TestSysRole implements SysRole {

    public String role;

    public TestSysRole(String role) {
        this.role = role;
    }

    @Override
    public String getRole() {
        return this.role;
    }

}
