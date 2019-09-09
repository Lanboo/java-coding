package com.xych.rabbitmq.api.dlq;

import java.io.IOException;

import com.rabbitmq.client.AMQP;

public class DlxProducer extends BaseRabbitMQ {
    public static void main(String[] args) throws Exception {
        new DlxProducer().run();
        Thread.sleep(1000);
    }
    
    @Override
    protected void doRun() {
        try {
            String msgTemp = "This is TTL msg!";
            // 对每条消息设置过期时间
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()//
                    .deliveryMode(2) // 持久化消息
                    .contentEncoding("UTF-8") //
                    .expiration("20000") // TTL
                    .build();
            // 发送消息
            // 默认交换机，routingKey填写队列名称
            for(int i = 0; i < 10; i++) {
                String msg = msgTemp + i;
                System.out.println(msg);
                channel.basicPublish("", QUEUE_NAME, properties, msg.getBytes());
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
