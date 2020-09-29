package com.len.boot.redis.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: jone
 * @date: Created in 2020/9/25 10:37 上午
 * @description:
 */
@Slf4j
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> cache;

    public boolean add(String key, Object value) {
        boolean result = false;
        try {
            cache.opsForValue().set(key, value);
            result = true;
        } catch (Exception e) {
            log.error("add key [{}], val [{}]", key, value, e);
        }
        return result;
    }

    public boolean add(String key, Object value, int minute) {
        boolean result = false;

        try {
            cache.opsForValue().set(key, value, minute, TimeUnit.MINUTES);
            result = true;
        } catch (Exception e) {
            log.error("add key [{}], val [{}], expire [{}]", key, value, minute, e);
        }

        return result;

    }

    public  boolean set(String key, Object value) {
        boolean result = false;
        try {
            cache.opsForValue().set(key, value);
            result = true;
        } catch (Exception e) {
            log.error("set key [{}], val [{}]", key, value, e);
        }
        return result;
    }

    public boolean set(String key, Object value, int minute) {
        boolean result = false;
        try {
            cache.opsForValue().set(key, value, minute, TimeUnit.MINUTES);
            result = true;
        } catch (Exception e) {
            log.error("set key [{}], val [{}]", key, value, e);
        }

        return result;

    }

    public  boolean replace(String key, Object value) {
        boolean result = false;
        try {
            cache.opsForValue().set(key, value);
            result = true;
        } catch (Exception e) {
            log.error("replace key [{}], val [{}]", key, value, e);
        }

        return result;

    }

    public  boolean replace(String key, Object value, int minute) {
        boolean result = false;
        try {
            cache.opsForValue().set(key, value, minute, TimeUnit.MINUTES);
            result = true;
        } catch (Exception e) {
            log.error("replace key [{}], val [{}], expire [{}]", key, value, minute, e);
        }

        return result;
    }

    public Object get(String key) {
        return cache.opsForValue().get(key);
    }

    public boolean delete(String key) {
        boolean result = false;
        try {
            cache.delete(key);
            result = true;
        } catch (Exception e) {
            log.error("delete key [{}]", key, e);
        }

        return result;
    }


    /**
     * 获取list缓存的内容
     * @param key 键
     * @return
     */
    public <T> List<T> lGet(String key, Class<T> tClass) {
        try {
            Object obj = get(key);
            if (null != obj){
                List<T> list = JSON.parseArray(obj.toString(),tClass);
                return list;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 将Object放入缓存
     * @param key 键
     * @param value 值
     * @return
     */

    public boolean lSet(String key, Object value) {
        try {
            cache.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key 键
     * @param value 值
     * @param minute 时间(分钟)
     * @return
     */
    public <T> boolean lSet(String key, List<T> value, int minute) {
        try {
            cache.opsForValue().set(key,JSON.toJSON(value), minute, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 指定缓存失效时间
     * @param key 键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                cache.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * redis 清楚某类key
     * @param pattern
     * @return
     */
    public boolean clear(String pattern) {
        try {
            Set<String> keys = cache.keys(pattern);
            cache.delete(keys);
            return true;
        }catch (Exception e) {
            log.error("delete keys [{}]", pattern, e);

            return false;
        }
    }

    /**
     * ttl
     * @param pattern
     * @return
     */
    public boolean ttl(String pattern) {
        try {
            Set<String> keys = cache.keys(pattern);
            for (String key :
                    keys) {
                // 当 key 存在但没有设置剩余生存时间时，返回 -1
                if (cache.getExpire(key) == -1) {
                    cache.expire(key,10, TimeUnit.SECONDS);
                }

            }
            return true;
        }catch (Exception e) {
            log.error("ttl keys [{}]", pattern, e);

            return false;
        }
    }

}
