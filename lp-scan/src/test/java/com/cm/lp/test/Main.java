package com.cm.lp.test;


import com.cn.lp.loader.ClassFilter;
import com.cn.lp.loader.ClassScanner;
import com.cn.lp.loader.SubOfClassFilter;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        ClassScanner scanner = new ClassScanner();
        // 添加过滤器，获取所有继承ClassFilter的类
        scanner.addFilter(SubOfClassFilter.ofInclude(ClassFilter.class));
        for (Class<?> data : scanner.getClasses("com.cn.lp")) {
            System.out.println(data);
        }
    }

}
