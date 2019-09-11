package com.xych.rabbitmq.spring.producer;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {
    private static final String EXCHANGE_1 = "SPRING_EXCHANGE_1";
    private static final String EXCHANGE_2 = "SPRING_EXCHANGE_2";
    private static final String ROUTING_KEY_1 = "spring_routing_key_1";
    private static final String ROUTING_KEY_2 = "spring_routing_key_2";
    private static final String ROUTING_KEY_3 = "A.Third.B";
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(Integer i) {
        this.rabbitTemplate.convertAndSend(EXCHANGE_1, ROUTING_KEY_1, initMsg("first", i));
        this.rabbitTemplate.convertAndSend(EXCHANGE_1, ROUTING_KEY_2, initMsg("second", i));
        this.rabbitTemplate.convertAndSend(EXCHANGE_2, ROUTING_KEY_3, initMsg("three", i));
    }

    private String initMsg(String queueIndex, Integer i) {
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        switch(queueIndex) {
            case "first":
                return "first message_" + i + "_" + time;
            case "second":
                return "second message_" + i + "_" + time;
            case "three":
                return "three message_" + i + "_" + time;
            default:
                return "";
        }
    }
}
