package com.cn.lp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@SpringBootApplication
@ServletComponentScan
@EnableCaching
public class LpAccountApplication extends SpringBootServletInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
    }

    public static void main(String[] args) {
        SpringApplication.run(LpAccountApplication.class, args);
    }

}
