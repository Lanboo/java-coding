package com.xych.kafka.springboot;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KafkaConsumerDemo {
    @KafkaListener(topics = { "test" })
    public void listen(ConsumerRecord<Integer, String> record) throws Exception {
        // System.out.printf("topic=%s,offset=%d,value=%s\n", record.topic(), record.offset(), record.value());
        log.info("Consumer:topic={},value={},offset={}", record.topic(), record.value(), record.offset());
    }
}
