[toc]

# ActiveMQ消息传送机制以及ACK机制详解

> 很有帮助的一篇文章：[ActiveMQ消息传送机制以及ACK机制详解](https://shift-alt-ctrl.iteye.com/blog/2020182) <br>
> 很有帮助的一篇文章：[ActiveMQ中Consumer特性详解与优化](https://shift-alt-ctrl.iteye.com/blog/2035321) <br>
> 本文章学自`咕泡学院`，并参考上面`QING____`的文章


## 1、ActiveMQ消息传送机制
### 1.1、一条消息的生命周期

![](http://dl2.iteye.com/upload/attachment/0094/2424/58b2e84f-1cba-30fd-99ae-e6c3a6620e12.jpg)<br>
- 图片中简单的描述了一条消息的生命周期,不过在不同的架构环境中,message的流动行可能更加复杂<br>
- 一条消息从producer端发出之后，一旦被broker正确保存，那么它将会被consumer消费，然后ACK，broker端才会删除；不过当消息过期或者存储设备溢出时，也会终结它。
### 1.2、ActiveMQ消息传送机制
![](http://dl2.iteye.com/upload/attachment/0094/2626/e69764da-fd5e-3cdb-a412-d17452f09063.jpg)

上图非常复杂，具体的原理，我们将会在下文中逐个解释。大概原理与过程： <br>
- 通过Connection实例创建Session之后，将会把session实例保存在本地的list中,即connection持有session列表，并在底层开启transport（例如TcpTransport实现Runnable接口），侦听数据。
- Session创建Consumer之后，将会把Consumer实例添加到本地的list中，以便此后分拣消息，即session持有consumer列表；此外connection中也持有一个consumer集合(Map)，其中Key为consumerId，value为session引用。
- 如果Session支持异步转发(asyncDispatch)或者使用了转发池(dispatchPool)，将创建线程池用来转发消息。
- 当broker端有消息通过transport发送时，connection将会分拣消息，根据消息中指定的consumerId，从本地session列表中获取其对应的session实例；然后将消息交付session负责转发。
- 当session接受到消息后，会根据消息的consumerId，在本地consumer列表中找到对应的consumer实例；检测session的消息转发方式，如果是同步转发，则直接将消息交给consumer，即调用consumer.dispatch(message),此方法要么调用messageListener.onMessage()，要么将消息添加到本地的unconsumedMessages队列中(唤醒receive)；如果是异步转发，则将消息添加到session级别的队列中并由线程池负责转发。
- 当consumer接受到消息之后，将会调用messageListener.onMessage方法或者从receive方法中返回。
- 当消息消费成功后，consumer将会根据指定的ACK_MODE负责向broker发送ACK指令，此后消息将会在broker端清除。


<!-->
- 同步阻塞式获取消息
    ``` java
    // 消息消费者
    MessageConsumer messageConsumer = session.createConsumer(destination);
    while(true) {
        // 阻塞式接收消息
        TextMessage message = (TextMessage) messageConsumer.receive();
        System.out.println(Thread.currentThread().getName() + "：by receive：" + message.getText());
    }
    ```
- 异步监听器获取消息
    ``` java
    // 消息消费者
    MessageConsumer messageConsumer = session.createConsumer(destination);
    // 监听器获取消息
    messageConsumer.setMessageListener(new MessageListener() {
        @Override
        public void onMessage(Message message) {
            try {
                TextMessage textMessage = (TextMessage) message;
                System.out.println(Thread.currentThread().getName() + "：by listener：" + textMessage.getText());
            }
            catch(JMSException e) {
                e.printStackTrace();
            }
        }
    });
    ```
- 两者不能同时使用，会报错
- 最大的原因还是在事务性会话中，两种消费模式的事务不好管控