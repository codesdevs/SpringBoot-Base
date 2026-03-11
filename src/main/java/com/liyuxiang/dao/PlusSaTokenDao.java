package com.liyuxiang.dao;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.util.SaFoxUtil;
import com.liyuxiang.common.utils.RedisUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Sa-Token Redis 持久层实现
 */
public class PlusSaTokenDao implements SaTokenDao {

    /* ------------------------ String ------------------------ */

    @Override
    public String get(String key) {
        return RedisUtils.getCacheObject(key);
    }

    @Override
    public void set(String key, String value, long timeout) {
        if (timeout == 0 || timeout <= NOT_VALUE_EXPIRE) {
            return;
        }

        if (timeout == NEVER_EXPIRE) {
            RedisUtils.setCacheObject(key, value);
        } else {
            RedisUtils.setCacheObject(key, value, Duration.ofSeconds(timeout));
        }
    }

    @Override
    public void update(String key, String value) {
        long expire = getTimeout(key);
        if (expire == NOT_VALUE_EXPIRE) {
            return;
        }
        set(key, value, expire);
    }

    @Override
    public void delete(String key) {
        RedisUtils.deleteObject(key);
    }

    @Override
    public long getTimeout(String key) {
        long timeout = RedisUtils.getTimeToLive(key);
        return timeout < 0 ? timeout : timeout / 1000;
    }

    @Override
    public void updateTimeout(String key, long timeout) {
        if (timeout == NEVER_EXPIRE) {
            long expire = getTimeout(key);
            if (expire != NEVER_EXPIRE) {
                set(key, get(key), timeout);
            }
            return;
        }
        RedisUtils.expire(key, Duration.ofSeconds(timeout));
    }

    /* ------------------------ Object ------------------------ */

    @Override
    public Object getObject(String key) {
        return RedisUtils.getCacheObject(key);
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        return RedisUtils.getCacheObject(key);
    }

    @Override
    public void setObject(String key, Object object, long timeout) {
        if (timeout == 0 || timeout <= NOT_VALUE_EXPIRE) {
            return;
        }

        if (timeout == NEVER_EXPIRE) {
            RedisUtils.setCacheObject(key, object);
        } else {
            RedisUtils.setCacheObject(key, object, Duration.ofSeconds(timeout));
        }
    }

    @Override
    public void updateObject(String key, Object object) {
        long expire = getObjectTimeout(key);
        if (expire == NOT_VALUE_EXPIRE) {
            return;
        }
        setObject(key, object, expire);
    }

    @Override
    public void deleteObject(String key) {
        RedisUtils.deleteObject(key);
    }

    @Override
    public long getObjectTimeout(String key) {
        long timeout = RedisUtils.getTimeToLive(key);
        return timeout < 0 ? timeout : timeout / 1000;
    }

    @Override
    public void updateObjectTimeout(String key, long timeout) {
        if (timeout == NEVER_EXPIRE) {
            long expire = getObjectTimeout(key);
            if (expire != NEVER_EXPIRE) {
                setObject(key, getObject(key), timeout);
            }
            return;
        }
        RedisUtils.expire(key, Duration.ofSeconds(timeout));
    }

    /* ------------------------ Session ------------------------ */

    @Override
    public SaSession getSession(String sessionId) {
        return getObject(sessionId, SaSession.class);
    }

    @Override
    public void setSession(SaSession session, long timeout) {
        setObject(session.getId(), session, timeout);
    }

    @Override
    public void updateSession(SaSession session) {
        updateObject(session.getId(), session);
    }

    @Override
    public void deleteSession(String sessionId) {
        deleteObject(sessionId);
    }

    @Override
    public long getSessionTimeout(String sessionId) {
        return getObjectTimeout(sessionId);
    }

    @Override
    public void updateSessionTimeout(String sessionId, long timeout) {
        updateObjectTimeout(sessionId, timeout);
    }

    /* ------------------------ 搜索 ------------------------ */

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        Collection<String> keys = RedisUtils.keys(prefix + "*" + keyword + "*");
        List<String> list = new ArrayList<>(keys);
        return SaFoxUtil.searchList(list, start, size, sortType);
    }
}