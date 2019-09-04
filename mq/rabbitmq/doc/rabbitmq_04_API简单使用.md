[toc]

# RabbitMQ的JavaAPI简单使用

``` xml
<dependency>
    <groupId>com.rabbitmq</groupId>
    <artifactId>amqp-client</artifactId>
    <version>5.6.0</version>
</dependency>
```

## 1、BaseAbstract
``` java
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
```

## 2、Producer
``` java
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RabbitMQProducer extends BaseRabbitMQ {
    public static void main(String[] args) throws InterruptedException {
        new RabbitMQProducer().run();
        Thread.sleep(1000);
    }

    @Override
    public void doRun() {
        try {
            String msg = "Simple Msg_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            System.out.println(msg);
            channel.basicPublish(EXCHANGE, ROUTING_KEY, null, msg.getBytes());
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
```

## 3、Consumer
``` java
import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RabbitMQConsumer extends BaseRabbitMQ {
    public static void main(String[] args) throws InterruptedException {
        new RabbitMQConsumer().run();
        Thread.sleep(10000);
    }

    @Override
    protected void doRun() {
        try {
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
            String result = channel.basicConsume(QUEUE_NAME, true, consumer);
            System.out.println("Consume result:" + result);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
```