package com.cn.lp.entity;

import javax.validation.constraints.Email;

/**
 * Created by qirong on 2019/11/9.
 */
public class User {

    @Email(message = "邮箱格式错误")
    String firstname;

    String lastname;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

}
