package com.cn.lp.role;

import java.util.List;

/**
 * 获取用户权限接口
 * @author qirong
 * @date 2018/9/18
 */
public interface SysPermissionService {

    List<SysPermission> selectPermByInfo(AccountInfo accountInfo);

}
