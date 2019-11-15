package com.xych.id.generator;

public interface IdGenerator {
    Long[] nextLongId(int var1);

    Long nextLongId();

    String[] nextStringId(int var1);

    String nextStringId();
}
