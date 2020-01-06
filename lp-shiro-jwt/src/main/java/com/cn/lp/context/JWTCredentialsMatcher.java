package com.cn.lp.context;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.cn.lp.utils.JwtUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;


/**
 * JWT 校验
 */
public class JWTCredentialsMatcher implements CredentialsMatcher {

    private final Logger log = LoggerFactory.getLogger(JWTCredentialsMatcher.class);

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        String token = (String) authenticationToken.getCredentials();
        Object stored = authenticationInfo.getCredentials();
        String salt = stored.toString();
        Long uid = (Long) authenticationInfo.getPrincipals().getPrimaryPrincipal();
        try {
            return JwtUtils.verifyToken(token, uid, salt);
        } catch (JWTVerificationException e) {
            log.error("Token Error:{}", e.getMessage());
        }

        return false;
    }

}
