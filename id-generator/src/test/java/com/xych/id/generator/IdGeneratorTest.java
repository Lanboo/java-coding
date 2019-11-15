package com.xych.id.generator;

import com.xych.id.strategy.CommonTimeStrategy;
import com.xych.id.strategy.SnowflakeStrategy;

public class IdGeneratorTest {
    public static void main(String[] args) {
        CommonHostNameIdGenerator idGenerator1 = new CommonHostNameIdGenerator(SnowflakeStrategy.class);
        System.out.println(idGenerator1.nextStringId());
        System.out.println(idGenerator1.nextStringId());
        System.out.println(idGenerator1.nextStringId());
        System.out.println(idGenerator1.nextStringId());
        System.out.println(idGenerator1.nextStringId());
        CommonHostNameIdGenerator idGenerator2 = new CommonHostNameIdGenerator(CommonTimeStrategy.class);
        System.out.println(idGenerator2.nextStringId());
        System.out.println(idGenerator2.nextStringId());
        System.out.println(idGenerator2.nextStringId());
        System.out.println(idGenerator2.nextStringId());
        System.out.println(idGenerator2.nextStringId());
        CommonHostNameIdGenerator idGenerator3 = new CommonHostNameIdGenerator(new CommonTimeStrategy("HHmmss", 3, 99999));
        System.out.println(idGenerator3.nextStringId());
        System.out.println(idGenerator3.nextStringId());
        System.out.println(idGenerator3.nextStringId());
        System.out.println(idGenerator3.nextStringId());
        System.out.println(idGenerator3.nextStringId());
    }
}
