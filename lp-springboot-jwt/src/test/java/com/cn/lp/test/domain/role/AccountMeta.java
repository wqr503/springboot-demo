package com.cn.lp.test.domain.role;

import com.cn.lp.AccountIdentifier;

/**
 * Created by on 2019/8/9.
 */
public class AccountMeta implements AccountIdentifier {

    public static String UID_PARAM_NAME = "uid";

    private long uid;

    public static AccountMeta of(long uid) {
        AccountMeta accountMeta = new AccountMeta();
        accountMeta.uid = uid;
        return accountMeta;
    }

    public Long getUid() {
        return uid;
    }

}
