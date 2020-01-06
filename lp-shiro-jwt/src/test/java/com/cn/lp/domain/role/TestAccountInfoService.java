package com.cn.lp.domain.role;


import com.cn.lp.role.AccountInfo;
import com.cn.lp.role.AccountInfoService;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by qirong on 2018/9/19.
 */
@Component
public class TestAccountInfoService implements AccountInfoService {

    private static AtomicInteger idCreator = new AtomicInteger();

    @Override
    public AccountInfo searchAccountInfo(String accountName) {
        return new TestAccountInfo(idCreator.get(), "admin", "admin", "lp", CommonAccountState.NORMAL);
    }

    @Override
    public AccountInfo getAccountInfo(long uid) {
        return new TestAccountInfo(idCreator.get(), "admin", "admin", "lp", CommonAccountState.NORMAL);
    }

}
