package com.cn.lp;

/**
 * Created by qirong on 2019/6/27.
 */
public class Hello {

    @NeedLogin
    public void sayHello() {
        throw new RuntimeException("e");
//        System.out.println("Hello, AspectJ!");
    }

    public static void main(String[] args) {
        Hello hello = new Hello();
        hello.sayHello();
    }
}