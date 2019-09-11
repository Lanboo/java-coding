package com.xych.rabbitmq.spring.consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThirdConsumer implements MessageListener {
    @Override
    public void onMessage(Message message) {
        log.info("Second Queue Consumer received message:{}", message);
    }
}
