package com.xych.rabbitmq.api.dlq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public abstract class BaseRabbitMQ {
    protected static final String QUEUE_NAME = "len_queue";
    protected static final String DL_EXCHANGE = "DL_EXCHANGE";
    protected static final String DL_ROUTING_KEY = "dl_routing_key";
    protected static final String DL_QUEUE_NAME = "dl_queue";
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
        // 声明死信交换机
        channel.exchangeDeclare(DL_EXCHANGE, BuiltinExchangeType.DIRECT);
        // 声明死信队列
        channel.queueDeclare(DL_QUEUE_NAME, false, false, false, null);
        // 死信交换机和死信队列的绑定关系
        channel.queueBind(DL_QUEUE_NAME, DL_EXCHANGE, DL_ROUTING_KEY);
        //
        // 以上是准备一个死信队列
        // 以下是声明一个有死信队列的正常队列
        //        
        // 通过队列属性设置消息过期时间
        Map<String, Object> argss = new HashMap<String, Object>();
        // 队列的最大消息数
        argss.put("x-max-length", 5);
        // 队列的最大容量，单位Byte
        argss.put("x-max-length-bytes", 1024);
        // 队列溢出行为 drop-head:死信;reject-publish:拒绝新消息
        // argss.put("x-overflow", "drop-head");
        // argss.put("x-overflow", "reject-publish");
        // 指定死信的交换机
        argss.put("x-dead-letter-exchange", DL_EXCHANGE);
        // 指定死信的路由键
        argss.put("x-dead-letter-routing-key", DL_ROUTING_KEY);
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
