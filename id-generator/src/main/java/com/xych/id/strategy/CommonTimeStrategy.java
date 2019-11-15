package com.xych.id.strategy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 通用ID生成器
 * 格式:时间+主机号+轮询数
 * 默认格式:yyyyMMddHHmmssSSS+3位主机号+0000到9999轮询
 */
@Slf4j
public class CommonTimeStrategy extends AbstractGenerateStrategy {
    /**
     * 时间格式
     */
    protected String SEQUENCE_TIME_PATTERN = "yyyyMMddHHmmssSSS";
    /**
     * 主机号长度
     */
    protected int WORKER_ID_MAX_LEN = 3;
    /**
     * 轮询最大值，超过此值从1开始
     */
    protected int SEQUENCE_MAX_VALUE = 9999;
    /**
     * 轮询数据的长度，前面补0
     */
    protected int SEQUENCE_MAX_LEN = 4;
    protected String workerId_txt;
    protected AtomicInteger sequence = new AtomicInteger(1);
    protected long lastTime;

    public CommonTimeStrategy() {
        super();
    }

    /**
     * @param pattern 时间格式
     * @param worderIdLen worderId长度
     * @param maxSeq 轮询序列的最大值
     */
    public CommonTimeStrategy(String pattern, Integer worderIdLen, Integer maxSeq) {
        this.SEQUENCE_TIME_PATTERN = pattern;
        this.WORKER_ID_MAX_LEN = worderIdLen;
        this.SEQUENCE_MAX_VALUE = maxSeq;
        this.SEQUENCE_MAX_LEN = maxSeq.toString().length();
    }

    @Override
    public String nextId() {
        long currentMillis = System.currentTimeMillis();
        if(currentMillis < this.lastTime) {
            String message = String.format("Clock moved backwards. Refusing to generate id for %d milliseconds.", new Object[] { Long.valueOf(this.lastTime - currentMillis) });
            log.error(message);
            throw new RuntimeException(message);
        }
        int value = this.sequence.get();
        if(value > SEQUENCE_MAX_VALUE) {
            currentMillis = this.waitUntilNextMillis(this.lastTime);
            this.sequence = new AtomicInteger(1);
        }
        this.lastTime = currentMillis;
        StringBuffer sb = new StringBuffer();
        sb.append(new SimpleDateFormat(SEQUENCE_TIME_PATTERN).format(new Date(currentMillis)));
        sb.append(this.workerId_txt);
        sb.append(StringUtils.leftPad(String.valueOf(this.sequence.getAndIncrement()), SEQUENCE_MAX_LEN, '0'));
        return sb.toString();
    }

    @Override
    public void setWorkerId(long workerId) {
        this.workerId_txt = StringUtils.leftPad(String.valueOf(workerId), WORKER_ID_MAX_LEN, '0');
        int len = this.workerId_txt.length();
        if(WORKER_ID_MAX_LEN <= 0) {
            this.workerId_txt = "";
        }
        else if(len > WORKER_ID_MAX_LEN) {
            this.workerId_txt = this.workerId_txt.substring(len - WORKER_ID_MAX_LEN);
        }
    }
}
