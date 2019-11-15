package com.xych.id.strategy;

public interface IdGenerateStrategy {
    String nextId();

    void setWorkerId(long workerId);
}