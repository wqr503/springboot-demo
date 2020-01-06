package com.cn.lp.shiro.domain;


/**
 * 账户状态
 * Created by qirong on 2018/10/20.
 */
public enum AccountState {

    /**
     * 普通
     */
    NORMAL(0),

    /**
     * 锁定
     */
    LOCK(1),

    ;

    private int stateID;

    AccountState(int stateID) {
        this.stateID = stateID;
    }

    public Integer getID() {
        return this.stateID;
    }

    public static AccountState getStateByID(int stateID) {
        for (AccountState state : AccountState.values()) {
            if (state.getID() == stateID) {
                return state;
            }
        }
        return null;
    }
}
