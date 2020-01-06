package com.cn.lp.shiro.context;


import com.cn.lp.shiro.annotation.ShiroDefinition;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

/**
 * shiro 拦截持有者管理器
 *
 * @author qirong
 * @date 2018/5/24
 */
@Component
public class ShiroDefinitionHolderManager implements ApplicationContextAware {

    private Map<String, String> shiroDefinitionMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        for (Object controller : applicationContext.getBeansWithAnnotation(Controller.class).values()) {
            List<String> heads = new ArrayList<>();
            RestController restController = controller.getClass().getAnnotation(RestController.class);
            if (restController == null) {
                RequestMapping requestMapping = controller.getClass().getAnnotation(RequestMapping.class);
                if (requestMapping != null) {
                    for (String head : requestMapping.value()) {
                        heads.add(head);
                    }
                } else {
                    heads.add("");
                }
            } else {
                heads.add(restController.value());
            }
            List<Method> methods = getDeepMethod(controller.getClass());
            for (Method method : methods) {
                List<String> urls = this.getUrl(method);
                ShiroDefinition shiroDefinition = method.getAnnotation(ShiroDefinition.class);
                if (shiroDefinition != null) {
                    for (String url : urls) {
                        for (String head : heads) {
                            shiroDefinitionMap.put(formatUrl(head, url), shiroDefinition.filter());
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取所有方法(包括父类，但是不包括Object的方法),获取到的方法不包括继承的方法
     *
     * @param clazz
     * @return
     */
    public static List<Method> getDeepMethod(Class<?> clazz) {
        List<Method> methodList = new ArrayList<Method>();
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);
            methodList.add(method);
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            methodList.addAll(getDeepMethod(superClass));
        }
        return methodList;
    }

    private String formatUrl(String head, String url) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("/");
        if (head != null && !head.isEmpty()) {
            urlBuilder.append(head).append("/");
        }
        if (url != null && !url.isEmpty()) {
            url = url.replaceAll("\\{[^}]*\\}", "**");
            urlBuilder.append(url);
        }
        return urlBuilder.toString();
    }

    public Map<String, String> getShiroDefinitionMap() {
        return Collections.unmodifiableMap(this.shiroDefinitionMap);
    }

    private List<String> getUrl(Method method) {
        List<String> urls = new ArrayList<>();
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            for (String url : getMapping.value()) {
                urls.add(url);
            }
        }
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            for (String url : putMapping.value()) {
                urls.add(url);
            }
        }
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            for (String url : deleteMapping.value()) {
                urls.add(url);
            }
        }
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            for (String url : postMapping.value()) {
                urls.add(url);
            }
        }
        PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
        if (patchMapping != null) {
            for (String url : patchMapping.value()) {
                urls.add(url);
            }
        }
        RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
        if (methodRequestMapping != null) {
            for (String url : methodRequestMapping.value()) {
                urls.add(url);
            }
        }
        return urls;
    }

}
