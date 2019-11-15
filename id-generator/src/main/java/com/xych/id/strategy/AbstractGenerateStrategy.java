package com.xych.id.strategy;

public abstract class AbstractGenerateStrategy implements IdGenerateStrategy {
    protected long waitUntilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while(timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
