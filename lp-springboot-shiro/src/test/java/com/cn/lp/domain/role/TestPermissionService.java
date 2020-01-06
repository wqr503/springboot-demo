package com.cn.lp.domain.role;


import com.cn.lp.shiro.role.AccountInfo;
import com.cn.lp.shiro.role.SysPermission;
import com.cn.lp.shiro.role.SysPermissionService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qirong on 2018/9/19.
 */
@Component
public class TestPermissionService implements SysPermissionService {

    @Override
    public List<SysPermission> selectPermByInfo(AccountInfo accountInfo) {
        List<SysPermission> permissionList = new ArrayList<>();
        permissionList.add(new TestSysPermission("read"));
        return permissionList;
    }

}
