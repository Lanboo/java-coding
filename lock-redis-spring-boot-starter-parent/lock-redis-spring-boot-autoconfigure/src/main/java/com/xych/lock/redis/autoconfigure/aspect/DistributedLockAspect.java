package com.xych.lock.redis.autoconfigure.aspect;

import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.xych.lock.redis.autoconfigure.annotations.LockAction;
import com.xych.lock.redis.autoconfigure.annotations.LockAction.LockFailAction;
import com.xych.lock.redis.autoconfigure.lock.DistributedLock;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Configuration
@ConditionalOnClass(DistributedLock.class)
@Slf4j
public class DistributedLockAspect {
    @Autowired
    private DistributedLock distributedLock;
    private ExpressionParser parser = new SpelExpressionParser();
    private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @PostConstruct
    private void init() {
        log.info("distributed lock aspect init, lock impl={}", distributedLock.getClass());
    }

    @Pointcut("@annotation(com.kltong.inf.common.lock.redis.autoconfigure.annotations.LockAction)")
    private void lockPoint() {
    }

    @Around("lockPoint()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        LockAction lockAction = method.getAnnotation(LockAction.class);
        String key = lockAction.key();
        if(lockAction.isSpringEL()) {
            key = this.parse(key, method, pjp.getArgs());
        }
        else if(key.equals("'default'")) {
            key = "default";
        }
        // 获取锁
        int retryTimes = lockAction.action().equals(LockFailAction.CONTINUE) ? lockAction.retryTimes() : 0;
        boolean lock = distributedLock.lock(key, lockAction.keepMills(), retryTimes, lockAction.sleepMills());
        if(!lock) {
            log.debug("get lock failed : {} ", key);
            return null;
        }
        //得到锁,执行方法，释放锁
        log.debug("get lock success : {}", key);
        try {
            return pjp.proceed();
        }
        catch(Exception e) {
            log.error("execute locked method occured an exception", e);
            throw e;
        }
        finally {
            boolean releaseResult = distributedLock.releaseLock(key);
            log.debug("release lock : {}, result : {}", key, releaseResult ? " success" : " failed");
        }
    }

    /**
     * 解析spring EL表达式
     * @Author WeiXiaowei
     * @CreateDate 2019年3月8日下午3:05:41
     */
    private String parse(String key, Method method, Object[] args) {
        String[] params = discoverer.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();
        for(int i = 0; i < params.length; i++) {
            context.setVariable(params[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }
}
