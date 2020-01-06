package com.cn.lp.role;

/**
 * 获取用户接口
 * @author qirong
 * @date 2018/9/18
 */
public interface AccountInfoService {

    AccountInfo searchAccountInfo(String accountName);

    AccountInfo getAccountInfo(long uid);

}
