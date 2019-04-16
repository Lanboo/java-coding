package com.xych.lock.redis.spring.boot.starter.test.constans;

public class TaskConstans
{
    /**
     * 分布式锁key的前缀
     */
    public static final String LOCK_KEY_PREFIX = "XYCH:LOCK:";
    /**
     * 分布式锁超时时间
     */
    public static final long LOCK_TASK_KEEP_MILLS = 50000L;
}
