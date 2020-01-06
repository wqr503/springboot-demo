package com.cn.lp.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * JWT工具类
 */
public class JwtUtils {

    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的签发时间
     */
    public static Date getIssuedAt(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getIssuedAt();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的用户名
     */
    public static Long getUid(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("uid").asLong();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 校验Token
     *
     * @param token   jwtToken
     * @param secrecy 保密内容，用作签名，例如用户密码
     * @return
     */
    public static boolean verifyToken(String token, long uid, String secrecy) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secrecy);
            JWTVerifier verifier = JWT.require(algorithm)
                .withClaim("uid", uid)
                .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            Date expiresAt = decodedJWT.getExpiresAt();
            if (Objects.nonNull(expiresAt)) {
                if (System.currentTimeMillis() > expiresAt.getTime()) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 生成签名,expireTime后过期
     *
     * @param uid  用户ID
     * @param time 过期时间s
     * @return 加密的token
     */
    public static String sign(long uid, String salt, long time) {
        try {
            Date issuedDate = new Date();
            Date expiresDate = new Date(System.currentTimeMillis() + time);
            Algorithm algorithm = Algorithm.HMAC256(salt);
            /**
             * withExpiresAt 设置过期时间 过期时间大于签发时间
             * withIssuedAt 设置签发时间
             */
            return JWT.create()
                .withClaim("uid", uid)
                .withExpiresAt(expiresDate)
                .withIssuedAt(issuedDate)
                .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * token是否过期
     *
     * @return true：过期
     */
    public static boolean isTokenExpired(String token) {
        Date now = Calendar.getInstance().getTime();
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().before(now);
    }

    /**
     * 生成随机盐,长度32位
     *
     * @return
     */
    public static String generateSalt() {
        SecureRandomNumberGenerator secureRandom = new SecureRandomNumberGenerator();
        String hex = secureRandom.nextBytes(16).toHex();
        return hex;
    }

}
