package com.cn.lp.domain.realm;


import com.cn.lp.domain.JWTShiroRealm;
import com.cn.lp.role.AccountInfoService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by qirong on 2019/4/17.
 */
public class NormalJWTRealm extends JWTShiroRealm {

    @Autowired
    private AccountInfoService accountInfoService;

    @Override
    protected AccountInfoService getAccountInfoService() {
        return accountInfoService;
    }

}
