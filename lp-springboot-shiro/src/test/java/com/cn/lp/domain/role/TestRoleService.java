package com.cn.lp.domain.role;


import com.cn.lp.shiro.role.AccountInfo;
import com.cn.lp.shiro.role.SysRole;
import com.cn.lp.shiro.role.SysRoleService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qirong on 2018/9/19.
 */
@Component
public class TestRoleService implements SysRoleService {

    @Override
    public List<SysRole> selectRoleByInfo(AccountInfo accountInfo) {
        List<SysRole> roleList = new ArrayList<>();
        roleList.add(new TestSysRole("admin"));
        return roleList;
    }
}
