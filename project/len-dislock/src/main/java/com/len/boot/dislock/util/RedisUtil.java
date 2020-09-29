package com.len.boot.dislock.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * CacheUtil 缓存工具类
 * @author wangning on 2020-08-11
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
    public <T> List<T> lGet(String key,Class<T> tClass) {
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


    /**
     * 加分布式锁
     *
     * @param track
     * @param sector
     * @param timeout
     * @return
     */
    public boolean setNx(String track, String sector, long timeout) {
        ValueOperations valueOperations = cache.opsForValue();
        Boolean flag = valueOperations.setIfAbsent(track + sector, System.currentTimeMillis());
        // 如果成功设置超时时间, 防止超时
        if (flag) {
            valueOperations.set(track + sector, getLockValue(track, sector), timeout, TimeUnit.SECONDS);
        }
        return flag;
    }

    /**
     * 删除锁
     *
     * @param track
     * @param sector
     */
    public void delete(String track, String sector) {
        cache.delete(track + sector);
    }

    /**
     * 查询锁
     * @return 写锁时间
     */
    public long getLockValue(String track, String sector) {
        ValueOperations valueOperations = cache.opsForValue();
        Object value = valueOperations.get(track + sector);
        log.info("redis中的锁创建时间是:{}",value);
        log.info("redis中的锁创建时间是:{}",(long)value);
        if(value==null){
            return 0L;
        }
        long createTime = (long) value;
        return createTime;
    }


}
