package com.xych.lock.redis.spring.boot.starter.test.task;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.xych.lock.redis.autoconfigure.annotations.LockAction;
import com.xych.lock.redis.spring.boot.starter.test.constans.TaskConstans;

import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@EnableAsync
@Slf4j
public class TestScheduledTask
{
    StopWatch stopWatch = new StopWatch();

    @Async
    @Scheduled(cron = "${schedule.refund.cron.test}")
    @LockAction(key = TaskConstans.LOCK_KEY_PREFIX + "TEST", keepMills = TaskConstans.LOCK_TASK_KEEP_MILLS)
    public void refund()
    {
        synchronized(this)
        {
            try
            {
                stopWatch.start();
                // 业务逻辑
                log.info("定时任务:开始");
            }
            finally
            {
                stopWatch.stop();
                log.info("定时任务:结束:时间消耗={}", stopWatch.getLastTaskTimeMillis());
            }
        }
    }
}
