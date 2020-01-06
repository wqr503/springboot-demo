package com.cn.lp.shiro.context;

import com.cn.lp.shiro.config.ShiroConfig;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * ShiroSessionDAO SessionDAO实现
 * @author qirong
 * @date 2019/3/24
 */
public class ShiroSessionDAO extends AbstractSessionDAO {

    private static Logger logger = LoggerFactory.getLogger(ShiroSessionDAO.class);
    /**
     * shiro-redis的session对象前缀
     */
    private ShiroCacheClient cacheClient;

    private ShiroConfig shiroConfig;

    /**
     * The Redis key prefix for the sessions
     */
    private String keyPrefix = "shiro_redis_session:";

    @Override
    public void update(Session session) throws UnknownSessionException {
        this.saveSession(session);
    }

    /**
     * save session
     *
     * @param session
     * @throws UnknownSessionException
     */
    private void saveSession(Session session) throws UnknownSessionException {
        if (session == null || session.getId() == null) {
            logger.error("session or session id is null");
            return;
        }
        this.cacheClient.set(getKey(session.getId()), session, shiroConfig.getSessionTimeOut());
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            logger.error("session or session id is null");
            return;
        }
        this.cacheClient.del(getKey(session.getId()));
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<Session> sessions = new HashSet<>();
        Set<String> keys = this.cacheClient.keys(this.keyPrefix + "*");
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                Session s = this.cacheClient.get(key);
                sessions.add(s);
            }
        }
        return sessions;
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        this.saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            logger.error("session id is null");
            return null;
        }
        Session s = this.cacheClient.get(this.getKey(sessionId));
        return s;
    }

    /**
     * 获得byte[]型的key
     *
     * @return
     */
    private String getKey(Serializable sessionId) {
        return this.keyPrefix + sessionId;
    }

    /**
     * Returns the Redis session keys
     * prefix.
     *
     * @return The prefix
     */
    public String getKeyPrefix() {
        return keyPrefix;
    }

    /**
     * Sets the Redis sessions key
     * prefix.
     *
     * @param keyPrefix The prefix
     */
    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public ShiroSessionDAO setCacheClient(ShiroCacheClient cacheClient) {
        this.cacheClient = cacheClient;
        return this;
    }

    public ShiroSessionDAO setShiroConfig(ShiroConfig shiroConfig) {
        this.shiroConfig = shiroConfig;
        return this;
    }

}
