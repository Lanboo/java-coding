package com.xych.kafka.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerDemo {
    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;
    
    public void send(String message) {
        this.kafkaTemplate.send("test", message);
    }
}
