package com.xych.lock.redis.autoconfigure.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LockAction {
    String key() default "'default'";

    /**
     * key 是否是Spring EL表达式
     */
    boolean isSpringEL() default false;

    /**
     * 持锁时间,单位毫秒
     */
    long keepMills() default 30000L;

    /**
     * 当获取失败时候动作
     */
    LockFailAction action() default LockFailAction.GIVEUP;

    public enum LockFailAction {
        /** 放弃 */
        GIVEUP,
        /** 继续 */
        CONTINUE;
    }

    /**
     * 重试的间隔时间,设置GIVEUP忽略此项
     */
    long sleepMills() default 200L;

    /**
     * 重试次数
     */
    int retryTimes() default 1;
}
