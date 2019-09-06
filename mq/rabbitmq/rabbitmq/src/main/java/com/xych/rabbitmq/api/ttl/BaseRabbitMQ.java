package com.xych.rabbitmq.api.ttl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public abstract class BaseRabbitMQ {
    protected static final String QUEUE_NAME = "ttl_queue";
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
        // 通过队列属性设置消息过期时间
        Map<String, Object> argss = new HashMap<String, Object>();
        argss.put("x-message-ttl", 20000);
        // 声明队列（默认交换机AMQP default，Direct）
        channel.queueDeclare(QUEUE_NAME, false, false, false, argss);
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
