[toc]

# RabbitMQ的消息过期时间

> 过期时间，TTL(Time To Live)

> <b>如果同时指定了Message TTL和Queue TTL，则小的那个时间生效。</b>

## 1、队列中的TTL
通过队列属性`x-message-ttl`设置消息过期时间
``` java
// 通过队列属性设置消息过期时间
Map<String, Object> argss = new HashMap<String, Object>();
argss.put("x-message-ttl", 20000);
// 声明队列（默认交换机AMQP default，Direct）
channel.queueDeclare(QUEUE_NAME, false, false, false, argss);
```

## 2、单条消息的TTL
``` java
// 对每条消息设置过期时间
AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()//
        .expiration("10000") // TTL
        .build();
// 发送消息
// 默认交换机，routingKey填写队列名称
channel.basicPublish("", QUEUE_NAME, properties, msg.getBytes());
```