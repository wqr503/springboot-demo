package com.cn.lp.shiro.config;

/**
 * Shiro 配置
 *
 * @author qirong
 * @date 2019/4/8
 */
public interface ShiroConfig {

    /**
     * 获取密码有效时间
     * @return
     */
    int getPasswordExpireTime();

    /**
     * 获取Session 过期时间
     * @return
     */
    long getSessionTimeOut();

    /**
     * 获取令牌有效时间
     * @return
     */
    int getTokenExpireTime();

    /**
     * 获取未登录转向网址
     * @return
     */
    String getUnloginUrl();

    /**
     * 获取无权限转向网址
     * @return
     */
    String getNotRoleUrl();

    /**
     * 获取RememberMe密钥
     * @return
     */
    String getRememberMeSecretKey();

    /**
     * 获取RememberMe有效时长
     * @return
     */
    long getRememberMeExpireTime();

}
