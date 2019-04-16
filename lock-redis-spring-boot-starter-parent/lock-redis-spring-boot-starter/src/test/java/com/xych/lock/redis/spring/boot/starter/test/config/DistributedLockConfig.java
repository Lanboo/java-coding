package com.xych.lock.redis.spring.boot.starter.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.xych.lock.redis.autoconfigure.lock.DistributedLock;
import com.xych.lock.redis.autoconfigure.lock.RedisDistributedLock;

@Configuration
public class DistributedLockConfig {
    @Bean
    public DistributedLock distributedLock(RedisTemplate<String, String> redisTemplate) {
        return new RedisDistributedLock(redisTemplate);
    }
}
