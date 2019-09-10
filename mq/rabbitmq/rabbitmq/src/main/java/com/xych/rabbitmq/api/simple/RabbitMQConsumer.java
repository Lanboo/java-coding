package com.xych.rabbitmq.api.simple;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

public class RabbitMQConsumer extends BaseRabbitMQ {
    public static void main(String[] args) throws InterruptedException {
        new RabbitMQConsumer().run();
    }

    @Override
    protected void doRun() {
        try {
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                    System.out.println("基础连接关闭：consumerTag : " + consumerTag);
                }

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String msg = new String(body, "UTF-8");
                    System.out.println("Received message : '" + msg + "'");
                    System.out.println("consumerTag : " + consumerTag);
                    System.out.println("deliveryTag : " + envelope.getDeliveryTag());
                }
            };
            // 开始获取消息
            String result = channel.basicConsume(QUEUE_NAME, true, consumer);
            System.out.println("Consume result:" + result);
            // 阻塞一下，防止连接关闭
            Thread.sleep(10000);
        }
        catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
