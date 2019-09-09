package com.xych.rabbitmq.api.ttl;

import java.io.IOException;

import com.rabbitmq.client.AMQP;

public class TtlProducer extends BaseRabbitMQ {
    public static void main(String[] args) throws Exception {
        new TtlProducer().run();
        Thread.sleep(1000);
    }
    
    @Override
    protected void doRun() {
        try {
            String msg = "This is TTL msg!";
            // 对每条消息设置过期时间
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()//
                    .deliveryMode(2) // 持久化消息
                    .contentEncoding("UTF-8") //
                    .expiration("10000") // TTL
                    .build();
            // 发送消息
            // 默认交换机，routingKey填写队列名称
            channel.basicPublish("", QUEUE_NAME, properties, msg.getBytes());
            System.out.println(msg);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
