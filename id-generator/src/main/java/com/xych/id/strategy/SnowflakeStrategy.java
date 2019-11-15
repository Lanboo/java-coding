package com.xych.id.strategy;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 字节占位：时间戳|主机号|顺序号
 * 
 * 主机号：workerId
 * 主机号bit长度：WORKER_ID_BITS
 * 
 * 顺序号：sequence
 * 顺序号bit长度：SEQUENCE_BITS
 * 顺序号最大值：(1 << SEQUENCE_BITS) -1
 * 
 * 当前时间戳：currentMillis
 * 纪元时间戳：EPOCH
 * 时间戳bit长度：64 - WORKER_ID_BITS - SEQUENCE_BITS
 * 
 * return：(currentMillis - EPOCH) << (WORKER_ID_BITS + SEQUENCE_BITS) | workerId << WORKER_ID_BITS | sequence << SEQUENCE_BITS
 * </pre>
 */
@Slf4j
public class SnowflakeStrategy extends AbstractGenerateStrategy {
    private final long WORKER_ID_BITS = 10L;
    private final long SEQUENCE_BITS = 12L;
    private final long SEQUENCE_MASK = (1 << SEQUENCE_BITS) - 1;
    private final long WORKER_ID_LEFT_SHIFT_BITS = SEQUENCE_BITS;
    private final long TIMESTAMP_LEFT_SHIFT_BITS = WORKER_ID_BITS + SEQUENCE_BITS;
    private final long WORKER_ID_MAX_VALUE = 1 << WORKER_ID_BITS;
    private long workerId;
    private long sequence = 0L;
    private long lastTime;
    /**
     * 纪元，时间戳开始时间
     */
    private long EPOCH = 1523289600000L;

    public SnowflakeStrategy() {
    }

    public SnowflakeStrategy(long epochMillis) {
        this.EPOCH = epochMillis;
    }

    @Override
    public synchronized String nextId() {
        long currentMillis = System.currentTimeMillis();
        if(currentMillis < this.lastTime) {
            String message = String.format("Clock moved backwards. Refusing to generate id for %d milliseconds.", this.lastTime - currentMillis);
            log.error(message);
            throw new RuntimeException(message);
        }
        else {
            if(this.lastTime == currentMillis) {
                this.sequence = ++this.sequence & SEQUENCE_MASK;
                if(this.sequence == 0L) {
                    currentMillis = waitUntilNextMillis(this.lastTime);
                }
            }
            else {
                this.sequence = 0L;
            }
            this.lastTime = currentMillis;
            if(log.isDebugEnabled()) {
                log.debug("{}-{}-{}", new Object[] { (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(new Date(currentMillis)), this.workerId, this.sequence });
            }
            return String.valueOf(currentMillis - EPOCH << TIMESTAMP_LEFT_SHIFT_BITS | this.workerId << WORKER_ID_LEFT_SHIFT_BITS | this.sequence);
        }
    }

    @Override
    public void setWorkerId(long workerId) {
        if(workerId > WORKER_ID_MAX_VALUE) {
            throw new RuntimeException(workerId + ": workerid Must be positive or less than " + this.WORKER_ID_MAX_VALUE);
        }
        this.workerId = workerId;
    }
}
