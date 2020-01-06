package com.cn.lp.domain.role;

import com.cn.lp.role.AccountInfo;
import com.cn.lp.role.AccountState;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * Created by qirong on 2018/9/19.
 */
public class TestAccountInfo implements AccountInfo {

    private long uid;

    private AccountState state;

    private String credentialsSalt;

    private String password;

    private String accountName;

    public TestAccountInfo(long uid, String accountName, String password, String credentialsSalt, AccountState state) {
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
    public AccountState getState() {
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
