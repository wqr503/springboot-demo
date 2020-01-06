package com.cn.lp.test.domain.role;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

/**
 * Created by qirong on 2019/6/3.
 */
@Component
public class AccountManager {

    public static Account TEST_ACCOUNT = new Account()
        .setUid(1)
        .setCredentialsSalt("lp")
        .setPermissionList(Lists.newArrayList(AccountPermission.TEST_PERMISSION_1));

    public Account getAccount(long uid) {
        return TEST_ACCOUNT;
    }

    public Account createAccount() {
        return TEST_ACCOUNT;
    }

}
