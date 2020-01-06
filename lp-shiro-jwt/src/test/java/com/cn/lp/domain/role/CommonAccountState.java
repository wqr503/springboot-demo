package com.cn.lp.domain.role;

import com.cn.lp.role.AccountState;

/**
 * Created by qirong on 2019/5/9.
 */
public enum CommonAccountState implements AccountState {

    NORMAL(1, false);

    int id;

    boolean lock;

    CommonAccountState(int id, boolean lock) {
        this.id = id;
        this.lock = lock;
    }

    @Override
    public Integer getID() {
        return id;
    }

    @Override
    public boolean isLock() {
        return lock;
    }
}
