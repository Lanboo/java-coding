[toc]

# 1、服务端流控
官方文档<br>
https://www.rabbitmq.com/configure.html<br>
https://www.rabbitmq.com/flow-control.html<br>
https://www.rabbitmq.com/disk-alarms.html<br>

> 当RabbitMQ 生产MQ 消息的速度远大于消费消息的速度时，会产生大量的消息堆积，占用系统资源，导致机器的性能下降。我们想要控制服务端接收的消息的数量，应该怎么做呢？

> 队列的属性：`x-max-length`、`x-max-length-bytes`，分别控制队列的最大消息数和字节数，当超过这两个限制时，会根据`x-overflow`的值，对新消息作出不同的反应<br>
>
> 详见[队列参数](rabbitmq_05_参数简介.md#2申明队列的参数)

## 1.1、内存控制

https://www.rabbitmq.com/memory.html<br>

> RabbitMQ 会在启动时检测机器的物理内存数值。默认当MQ 占用40% 以上内存时，MQ 会主动抛出一个内存警告并阻塞所有连接（Connections）。可以通过修改rabbitmq.config 文件来调整内存阈值，默认值是0.4

``` properties
# 相对设置
vm_memory_high_watermark.relative = 0.4
# 绝对设置，会覆盖相对设置
vm_memory_high_watermark.absolute = 1073741824
vm_memory_high_watermark.absolute = 2GB
```
## 1.2、磁盘控制
> 另一种方式是通过磁盘来控制消息的发布。当磁盘空间低于指定的值时（默认50MB），触发流控措施。

``` properties
# 相对设置  20%
disk_free_limit.relative = 2.0
# 绝对设置，会覆盖相对设置
disk_free_limit.absolute = 50000
disk_free_limit.absolute = 50MB
```

# 2、消费端限流

https://www.rabbitmq.com/consumer-prefetch.html

如果，消费者消费速率和生产者生产速率相差不多，那么服务器不会存储过多的消息。

但是当消费者消费速率慢(消费者数量少、单条消息处理时间长)的情况下，<b>如果我们希望在一定数量的消息消费完之前，不再推送消息过来，就要用到消费端的流量限制措施。</b>

可以基于Consumer 或者channel 设置`prefetch count`的值，含义为Consumer端的最大的unacked messages数目。当超过这个数值的消息未被确认，RabbitMQ会停止投递新的消息给该消费者。

- java编程
    ``` java
    channel.basicQos(2); // 如果超过2 条消息没有发送ACK，当前消费者不再接受队列消息
    // false：收到发送ACK
    channel.basicConsume(QUEUE_NAME, false, consumer);
    ```
- SimpleMessageListenerContainer
    ``` java
    container.setPrefetchCount(2);
    ```

- Spring Boot
    ``` properties
    spring.rabbitmq.listener.simple.prefetch=2
    ```
