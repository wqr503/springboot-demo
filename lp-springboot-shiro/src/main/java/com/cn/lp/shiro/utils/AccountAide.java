package com.cn.lp.shiro.utils;


import com.cn.lp.shiro.context.ShiroCacheClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
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
import java.util.UUID;

/**
 * 账号帮助工具
 * Created by qirong on 2018/10/8.
 */
public class AccountAide {

    public final static String SESSION_TOKEN_NAME = "ACCESSTOKEN";

    public final static String TOKEN_REFRESH_TIME = "_RefreshTime";

    public final static String LOGIN_TIME_NAME = "Login-Time";

    public final static String REMEMBER_ME_NAME = "shiro-rememberMe";

    private final static Cookie sessionTokenCookie;

    private static Logger logger = LoggerFactory.getLogger(AccountAide.class);

    static {
        Cookie cookie = new SimpleCookie(AccountAide.SESSION_TOKEN_NAME);
        cookie.setHttpOnly(true);
        sessionTokenCookie = cookie;
    }

    /**
     * session绑定令牌
     *
     * @param request
     * @param response
     * @param cacheClient
     * @param session
     * @param sessionTimeOut
     * @param tokenExpireTime
     */
    public static void bindToken(HttpServletRequest request, HttpServletResponse response, ShiroCacheClient cacheClient,
        Session session, long sessionTimeOut, long tokenExpireTime) {
        String tokenID = UUID.randomUUID().toString().replace("-", "");
        Long refreshTime = System.currentTimeMillis() + ((long) (tokenExpireTime * 0.5));
        session.setTimeout(sessionTimeOut);
        cacheClient.set(tokenID, session.getId().toString(), tokenExpireTime);
        cacheClient.set(tokenID + AccountAide.TOKEN_REFRESH_TIME, refreshTime, tokenExpireTime);
        javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(AccountAide.SESSION_TOKEN_NAME, tokenID);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        request.setAttribute(AccountAide.SESSION_TOKEN_NAME, tokenID);
        response.setHeader(AccountAide.SESSION_TOKEN_NAME, tokenID);
    }

    /**
     * 清除令牌
     * @param request
     * @param cacheClient
     */
    public static void clearToken(HttpServletRequest request, ShiroCacheClient cacheClient) {
        String tokenID = (String) request.getAttribute(AccountAide.SESSION_TOKEN_NAME);
        if (tokenID != null) {
            cacheClient.del(tokenID);
            cacheClient.del(tokenID + AccountAide.TOKEN_REFRESH_TIME);
        }
    }

    /**
     * 获取令牌
     *
     * @param request
     * @param response
     * @return
     */
    public static String getSessionToken(ServletRequest request, ServletResponse response) {
        String token = WebUtils.toHttp(request).getHeader(AccountAide.SESSION_TOKEN_NAME);
        if (!StringUtils.isBlank(token)) {
            return token;
        }
        if (!(request instanceof HttpServletRequest)) {
            logger.debug("Current request is not an HttpServletRequest - cannot get session token cookie.  Returning null.");
            return null;
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        return sessionTokenCookie.readValue(httpRequest, WebUtils.toHttp(response));
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
