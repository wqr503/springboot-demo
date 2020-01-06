package com.cn.lp.shiro.role;

import java.util.List;

/**
 * 获取用户角色接口
 * @author qirong
 * @date 2018/9/18
 */
public interface SysRoleService {

    List<SysRole> selectRoleByInfo(AccountInfo accountInfo);

}
