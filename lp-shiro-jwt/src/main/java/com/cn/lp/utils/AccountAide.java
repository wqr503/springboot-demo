package com.cn.lp.utils;


import com.cn.lp.role.AccountInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 账号帮助工具
 * Created by qirong on 2018/10/8.
 */
public class AccountAide {

    public final static String JWT_TOKEN_NAME = "lp-access_token";

    public final static String LOGIN_TIME_NAME = "lp-login_time";

    private final static Cookie jwtTokenCookie;

    private static Logger logger = LoggerFactory.getLogger(AccountAide.class);

    static {
        Cookie cookie = new SimpleCookie(AccountAide.JWT_TOKEN_NAME);
        cookie.setHttpOnly(true);
        jwtTokenCookie = cookie;

    }

    /**
     * 绑定Jwt令牌
     *
     * @param accountInfo
     * @param request
     * @param response
     * @param tokenExpireTime
     */
    public static void bindJwtToken(AccountInfo accountInfo, HttpServletRequest request, HttpServletResponse response, long tokenExpireTime) {
        //Shiro认证通过后会将user信息放到subject内，生成token并返回
        String newToken = JwtUtils.sign(accountInfo.getUid(), accountInfo.getCredentialsSalt(), tokenExpireTime);
        response.setHeader(JWT_TOKEN_NAME, newToken);
        request.setAttribute(AccountAide.JWT_TOKEN_NAME, newToken);
        javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(AccountAide.JWT_TOKEN_NAME, newToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 清除令牌
     */
    public static void clearToken(HttpServletResponse response) {
        javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(AccountAide.JWT_TOKEN_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 获取JWT令牌
     *
     * @param request
     * @param servletResponse
     * @return
     */
    public static String getJwtToken(ServletRequest request, ServletResponse servletResponse) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String header = httpRequest.getHeader(AccountAide.JWT_TOKEN_NAME);
        if (header == null || header.isEmpty()) {
            header = jwtTokenCookie.readValue(httpRequest, WebUtils.toHttp(servletResponse));
        }
        return StringUtils.removeStart(header, "Bearer ");
    }

    /**
     * MD5加密字符串,32位长
     *
     * @param source 要加密的内容
     * @return 加密后的内容
     */
    public static String MD5(String source, String salt) {
        return byte2HexStr(MD5Bit(source.getBytes(), salt.getBytes()));
    }

    /**
     * MD5加密Bit数据
     *
     * @param sources byte数组
     * @return 加密后的byte数组
     */
    public static byte[] MD5Bit(byte[]... sources) {
        try {
            // 获得MD5摘要算法的 MessageDigest对象
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            for (byte[] source : sources) {
                md5Digest.update(source);
            }
            // 获得密文
            return md5Digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String byte2HexStr(byte[] bytes) {
        int bytesLen = bytes.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        StringBuffer hexString = new StringBuffer(bytesLen * 2);
        for (int i = 0; i < bytesLen; i++) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                // 如果为1位 前面补个0
                hexString.append(0);
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
