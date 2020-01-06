package com.cn.lp.test.domain.role;

/**
 * Created by qirong on 2019/6/3.
 */
public enum AccountPermission implements SysPermission {

    TEST_PERMISSION_1(SysPermission.TEST_PERMISSION_1, "测试权限1"),

    TEST_PERMISSION_2(SysPermission.TEST_PERMISSION_2, "测试权限2"),

    ;

    private int id;

    private String desc;

    AccountPermission(int id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    public static AccountPermission getByID(int id) {
        for(AccountPermission permission : values()) {
            if(permission.getId() == id) {
                return permission;
            }
        }
        return null;
    }

}
