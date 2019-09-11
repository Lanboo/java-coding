package com.xych.rabbitmq.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.xych.rabbitmq.spring.producer.MessageProducer;

public class SendTest {
    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/applicationContext.xml");
        System.out.println(context);
        MessageProducer producer = context.getBean(MessageProducer.class);
        for(int i = 0; i < 10; i++) {
            producer.sendMessage(i);
            Thread.sleep(100);
        }
        Thread.sleep(10000);
    }
}
