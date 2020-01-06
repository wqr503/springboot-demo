package com.cn.lp.domain.role;

import com.cn.lp.shiro.role.AccountInfo;

/**
 * Created by qirong on 2018/9/19.
 */
public class TestAccountInfo implements AccountInfo {

    private long uid;

    private Integer state;

    private String credentialsSalt;

    private String password;

    private String accountName;

    public TestAccountInfo(long uid, String accountName, String password, String credentialsSalt, Integer state) {
        this.uid = uid;
        this.accountName = accountName;
        this.credentialsSalt = credentialsSalt;
        this.password = password;
        this.state = state;
    }

    @Override
    public String getAccountName() {
        return accountName;
    }

    @Override
    public Integer getState() {
        return this.state;
    }

    @Override
    public String getCredentialsSalt() {
        return this.credentialsSalt;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public long getUid() {
        return this.uid;
    }

}
