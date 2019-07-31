[toc]
## ActiveMQ消息的同步发送和异步发送

### 同步发送
> 发送者发送一条消息至ActiveMQ中间件，会阻塞，直到中间件反馈一个确认消息，表示这个消息已被处理。<br>
> 这个机制提供了消息的安全性保障，但是由于会阻塞，会影响到发送端的性能。
### 异步发送
> 发送者不需要阻塞式等待中间件的反馈确认消息，所以性能较高。<br>
> 但是<b>会出现消息丢失的情况</b><br>
> 所以使用异步发送的前提：允许数据丢失。

### ActiveMQ默认发送策略
>- 非持久化消息是异步发送的
>- 非事务模式下，持久化消息是同步发送的。
>- 事务模式下，持久化消息是异步发送的。
>- ps.1：默认情况下，消息是持久化的。
>- ps.2：由于异步发送效率比同步发送高，持久化消息尽量开始事务。

### 开启异步发送
``` java 
ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://127.0.0.1:61616?jms.useAsyncSend=true");

((ActiveMQConnectionFactory) connectionFactory).setUseAsyncSend(true);

((ActiveMQConnection) connection).setUseAsyncSend(true);
```

