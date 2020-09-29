package com.len.boot.dislock.aspect;

import com.len.boot.dislock.annotation.TaskLock;
import com.len.boot.dislock.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: jone
 * @date: Created in 2020/9/16 11:11 上午
 * @description:
 */

@Slf4j
@Aspect
@Component
public class TaskLockAspect {

    private static final Integer MAX_RETRY_COUNT = 3;
    private static final String LOCK_PRE_FIX = "lockPreFix";
    private static final String LOCK_KEY = "lockKey";
    private static final String TIME_OUT = "timeOut";
    /**
      * 防止多节点之间时间不一致而设置的保护时间4096
      */
    private static final int PROTECT_TIME = 2 << 11;

    @Autowired
    private RedisUtil redisUtil;

    @Pointcut("@annotation(com.len.boot.dislock.annotation.TaskLock)")
    public void taskLockAspect() {
    }

    @Around("taskLockAspect()")
    public void lockAroundAction(ProceedingJoinPoint proceeding) throws Exception {

        //获取task锁
        Boolean flag = this.getLock(proceeding, 0, System.currentTimeMillis());
        if (flag) {
            try {
                proceeding.proceed();
                Thread.sleep(PROTECT_TIME);
            } catch (Throwable throwable) {
                throw new RuntimeException("分布式锁执行发生异常" + throwable.getMessage(), throwable);
            } finally {
                // 删除锁
                this.delLock(proceeding);
            }
        } else {
            log.info("其他系统正在执行此项任务");
        }

    }

    /**
     * 获取锁
     *
     * @param proceeding
     * @return
     */
    private boolean getLock(ProceedingJoinPoint proceeding, int count, long currentTime) {
        //获取注解中的参数
        Map<String, Object> annotationArgs = this.getAnnotationArgs(proceeding);
        String lockPrefix = (String) annotationArgs.get(LOCK_PRE_FIX);
        String key = (String) annotationArgs.get(LOCK_KEY);
        long expire = (long) annotationArgs.get(TIME_OUT);
        if (StringUtils.isEmpty(lockPrefix) || StringUtils.isEmpty(key)) {
            // 此条执行不到
            throw new RuntimeException("TaskLock,锁前缀,锁名未设置");
        }
        if (redisUtil.setNx(lockPrefix, key, expire)) {
            log.info("获取到tasklock");
            return true;
        } else {
            // 如果当前时间与锁的时间差, 大于保护时间,则强制删除锁(防止锁死)
            long createTime = redisUtil.getLockValue(lockPrefix, key);
            if ((currentTime - createTime) > (expire * 1000 + PROTECT_TIME)) {
                count ++;
                if (count > MAX_RETRY_COUNT){
                    return false;
                }
                redisUtil.delete(lockPrefix, key);
                getLock(proceeding,count,currentTime);
            }
            return false;
        }
    }

    /**
     * 删除锁
     *
     * @param proceeding
     */
    private void delLock(ProceedingJoinPoint proceeding) {
        Map<String, Object> annotationArgs = this.getAnnotationArgs(proceeding);
        String lockPrefix = (String) annotationArgs.get(LOCK_PRE_FIX);
        String key = (String) annotationArgs.get(LOCK_KEY);
        redisUtil.delete(lockPrefix, key);
        log.info("删除tasklock");
    }

    /**
     * 获取锁参数
     *
     * @param proceeding
     * @return
     */
    private Map<String, Object> getAnnotationArgs(ProceedingJoinPoint proceeding) {
        Class target = proceeding.getTarget().getClass();
        Method[] methods = target.getMethods();
        String methodName = proceeding.getSignature().getName();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Map<String, Object> result = new HashMap<String, Object>();
                TaskLock redisLock = method.getAnnotation(TaskLock.class);
                result.put(LOCK_PRE_FIX, redisLock.lockPrefix());
                result.put(LOCK_KEY, redisLock.lockKey());
                result.put(TIME_OUT, redisLock.timeUnit().toSeconds(redisLock.timeOut()));
                return result;
            }
        }
        return null;
    }

    /**
     * 获取第一个String类型的参数为锁的业务参数
     *
     * @param proceeding
     * @return
     */
    @Deprecated
    public String getFirstArg(ProceedingJoinPoint proceeding) {
        Object[] args = proceeding.getArgs();
        if (args != null && args.length > 0) {
            for (Object object : args) {
                String type = object.getClass().getName();
                if ("java.lang.String".equals(type)) {
                    return (String) object;
                }
            }
        }
        return null;
    }



}
