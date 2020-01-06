package com.cn.lp.domain;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by on 2019/7/22.
 */
public class Test {

    private String name = "hello";

    public String getName() {
        return name;
    }

    public Test setName(String name) {
        this.name = name;
        return this;
    }

    public static int go(List<Integer> numList) {
        return numList.get(0);
    }
}
