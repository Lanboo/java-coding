package com.xych.rabbitmq.api.simple;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RabbitMQConsumer {
    private static final String EXCHANGE = "SIMPLE_EXCHANGE";
    private static final String ROUTING_KEY = "simple_exchange_routing_key";
    private static final String QUEUE_NAME = "simple_queue";
    private Connection conn;
    private Channel channel;

    public static void main(String[] args) throws InterruptedException {
        new RabbitMQConsumer().doRun();
        Thread.sleep(1000);
    }

    public void doRun() {
        try {
            create();
            init();
            // 创建消费者
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
                    String msg = new String(body, "UTF-8");
                    System.out.println("Received message : '" + msg + "'");
                    System.out.println("consumerTag : " + consumerTag);
                    System.out.println("deliveryTag : " + envelope.getDeliveryTag());
                }
            };
            // 开始获取消息
            channel.basicConsume(QUEUE_NAME, true, consumer);
        }
        catch(IOException | TimeoutException e) {
            e.printStackTrace();
        }
        finally {
            try {
                channel.close();
                conn.close();
            }
            catch(IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    private void init() throws IOException {
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.DIRECT, false, false, null);
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 声明绑定关系
        channel.queueBind(QUEUE_NAME, EXCHANGE, ROUTING_KEY);
    }

    private void create() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        // 服务器host
        factory.setHost("xych.online");
        // 端口号
        factory.setPort(5672);
        // 虚拟主机
        factory.setVirtualHost("/");
        // 用户&密码
        factory.setUsername("admin");
        factory.setPassword("admin");
        // 创建连接
        conn = factory.newConnection();
        // 创建消息通道
        channel = conn.createChannel();
    }
}
