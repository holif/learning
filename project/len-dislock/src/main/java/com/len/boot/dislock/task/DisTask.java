package com.len.boot.dislock.task;

import com.len.boot.dislock.annotation.TaskLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author: jone
 * @date: Created in 2020/9/18 9:19 上午
 * @description:
 */
@Slf4j
@Component
public class DisTask {

    @Scheduled(cron = "0 0/1 * * * *")
    @TaskLock(lockPrefix = "dis",lockKey = "_dis")
    public void disLock(){

        log.info("我是dis定时任务，定时任务执行一次");

    }

}
