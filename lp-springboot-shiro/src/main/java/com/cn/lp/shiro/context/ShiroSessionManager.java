package com.cn.lp.shiro.context;

import com.cn.lp.shiro.utils.AccountAide;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;


/**
 * ShiroSessionManager session管理者，WebSessionManager的实现
 *
 * @author qirong
 * @date 2018/9/18
 */
public class ShiroSessionManager extends DefaultWebSessionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroSessionManager.class);

    private ShiroCacheClient cacheClient;

    public ShiroSessionManager() {
        super();
    }

    public ShiroSessionManager setCacheClient(ShiroCacheClient cacheClient) {
        this.cacheClient = cacheClient;
        return this;
    }

    /**
     * 获取session
     * 优化单次请求需要多次访问缓存的问题
     */
    @Override
    protected Session retrieveSession(SessionKey sessionKey) {
        Serializable sessionId = getSessionId(sessionKey);
        ServletRequest request = null;
        if (sessionKey instanceof WebSessionKey) {
            request = ((WebSessionKey) sessionKey).getServletRequest();
        }

        if (request != null && null != sessionId) {
            Object sessionObj = request.getAttribute(sessionId.toString());
            if (sessionObj != null) {
                return (Session) sessionObj;
            }
        }
        Session session = super.retrieveSession(sessionKey);
        if (request != null && null != sessionId) {
            request.setAttribute(sessionId.toString(), session);
        }
        return session;
    }

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String token = AccountAide.getSessionToken(request, response);
        if (token != null) {
            try {
                LOGGER.debug("获取到Token : {}", token);
                String sessionID = cacheClient.get(token);
                request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "header");
                request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, sessionID);
                request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
                request.setAttribute(AccountAide.SESSION_TOKEN_NAME, token);
                if (response instanceof HttpServletResponse) {
                    ((HttpServletResponse) response).setHeader(AccountAide.SESSION_TOKEN_NAME, token);
                }
                return sessionID;
            } catch (Exception e) {
                LOGGER.error("获取sessionID异常", e);
            }
            return null;
        }
        return null;
    }

}
