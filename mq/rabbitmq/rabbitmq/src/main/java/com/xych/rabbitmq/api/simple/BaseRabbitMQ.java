package com.xych.rabbitmq.api.simple;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public abstract class BaseRabbitMQ {
    protected static final String EXCHANGE = "SIMPLE_EXCHANGE";
    protected static final String ROUTING_KEY = "simple_exchange_routing_key";
    protected static final String QUEUE_NAME = "simple_queue";
    protected Connection conn;
    protected Channel channel;

    protected abstract void doRun();

    public void run() {
        try {
            create();
            init();
            doRun();
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
        // PS：默认的guest/guest不允许远程访问RabbitMQ
        factory.setUsername("admin");
        factory.setPassword("admin");
        // 创建连接
        conn = factory.newConnection();
        // 创建消息通道
        channel = conn.createChannel();
    }
}
