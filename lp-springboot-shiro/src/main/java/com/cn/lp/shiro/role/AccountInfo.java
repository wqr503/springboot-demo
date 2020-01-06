package com.cn.lp.shiro.role;

import java.io.Serializable;

/**
 * 用户抽象接口
 * @author qirong
 * @date 2018/9/18
 */
public interface AccountInfo extends Serializable {

    String getAccountName();

    Integer getState();

    String getCredentialsSalt();

    String getPassword();

    long getUid();

}
