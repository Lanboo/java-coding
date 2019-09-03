package com.xych.rabbitmq.api.simple;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQProducer {
    private static final String EXCHANGE = "SIMPLE_EXCHANGE";
    private static final String ROUTING_KEY = "simple_exchange_routing_key";
    private static final String QUEUE_NAME = "simple_queue";
    private Connection conn;
    private Channel channel;

    public static void main(String[] args) throws InterruptedException {
        new RabbitMQProducer().doRun();
        Thread.sleep(1000);
    }

    public void doRun() {
        try {
            create();
            init();
            String msg = "Simple Msg_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            System.out.println(msg);
            channel.basicPublish(EXCHANGE, ROUTING_KEY, null, msg.getBytes());
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
        factory.setHost("www.xych.online");
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
