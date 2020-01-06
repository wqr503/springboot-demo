package com.cn.lp.test.domain.role;

import com.cn.lp.AccountIdentifier;
import com.cn.lp.Permission;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qirong on 2019/5/31.
 */
public class Account implements AccountIdentifier {

    private long uid;

    private List<Permission> permissionList = new ArrayList<>();

    private String credentialsSalt;

    public String getCredentialsSalt() {
        return credentialsSalt;
    }

    protected Account setCredentialsSalt(String credentialsSalt) {
        this.credentialsSalt = credentialsSalt;
        return this;
    }

    public Long getUid() {
        return uid;
    }

    protected Account setUid(long uid) {
        this.uid = uid;
        return this;
    }

    public List<Permission> getPermissions() {
        return permissionList;
    }

    protected List<Permission> getPermissionList() {
        return permissionList;
    }

    protected Account setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
        return this;
    }
}
