package com.len.boot.dislock.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: jone
 * @date: Created in 2020/9/16 11:09 上午
 * @description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface TaskLock {

    String lockPrefix() default "";
    String lockKey() default "";
    long timeOut() default 5;
    TimeUnit timeUnit() default TimeUnit.SECONDS;

}
