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


## 2、Consumer端获取消息代码示例
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
## 3、Consumer端参数设置
### 3.1、optimizeAcknowledge/optimizeAcknowledgeTimeOut
>- <b>可优化ACK策略，这个是Consumer端最重要的调优参数之一。</b><br>
>- optimizeAcknowledge表示是否开启“优化ACK选项”，当为true时，可以指定optimizeAcknowledgeTimeOut数值用来约束ACK最大延迟确认的时间。<br>
>- 我们通过optimizeAck，可以实现可靠的批量消息确认。
>- brokerUrl中，默认开启，timeout为300  
>  `tcp://localhost:61616?jms.optimizeAcknowledge=true&jms.optimizeAcknowledgeTimeOut=3000`
>- 如果我们的consumer足够快，且服务器端的消息足够多，且指定了合适的prefethSize，我们可以将timeout时间设置的稍微大一些。
>- 建议不要关闭此选项。
>- <b>即使当optimizeACK为true，也只会当session的ACK模式为AUTO_ACKNOWLEDGE时才会生效。</b>
>   - 即在其他类型的ACK模式时consumer端仍然不会“延迟确认”
>   - 即`consumer.optimizeAck = connection.optimizeACK && session.isAutoAcknowledge()`
>- 当`consumer.optimizeACK`有效时，如果客户端已经消费但尚未确认的消息(deliveredMessage)达到`prefetch * 0.65`，consumer端将会自动进行ACK；同时如果离上一次ACK的时间间隔，已经超过`optimizeAcknowledgeTimout`毫秒，也会导致自动进行ACK。
### 3.2、prefetchSize
>- 预获取消息数量，<b>重要的调优参数之一</b>。<br>
>- 当Consumer活跃时，broker将会批量发送prefetchSize条消息给Consumer，consumer也可以配合optimizeAcknowledge来批量确认它们。<br>
>- 批量传送，极大的提高了网络传输效率，此值默认为1000。<br>
>- 通常情况下，如果consumer数量较多，或者消费速度较慢，或者消息量较少时，我们设定prefetchSize为较小的值。 <br>
>   - 比如：在Queue模式下，broker将使用“轮询”的方式来平衡多个消费者之间的消息传送数量。如果消费者消费速度较慢，而且prefetchSize较大，这将不利于消息量在多个消费者之间平衡。
>- 使用messageListener方式异步侦听消息，将不能设定prefetchSize <= 0的任何值。
>- 使用receive方式，且prefetchSize = 0时，将触发Client端使用Pull机制<br>
>   - Pull机制：即每次receive调用，都会向Broker端发送Pull指令，如果broker端有消息才会转发，在这种情况下，Broker不会主动Push消息给client。 <br>

## 4、Consumer端的ACK机制
<div style = "font-size:13px;">

ACK_MODE|备注
:-|:-
Session.`SESSION_TRANSACTED`(0)|事务
Session.`AUTO_ACKNOWLEDGE`(1)|自动应答
Session.`CLIENT_ACKNOWLEDGE`(2)|手动应答
Session.`DUPS_OK_ACKNOWLEDGE`(3)|自动批量确认
ActiveMQSession.`INDIVIDUAL_ACKNOWLEDGE`(4)|- 单条消息确认<br>- AcitveMQ补充了一个自定义的ACK模式
</div>

- ACK模式描述了Consumer与broker确认消息的方式(时机)，比如当消息被Consumer接收之后，Consumer将在何时确认消息。
- 对于broker而言，只有接收到ACK指令，才会认为消息被正确的接收或者处理成功了，通过ACK，可以在consumer（/producer）与Broker之间建立一种简单的“担保”机制。

<div style = "font-size:13px;">

ACK_TYPE|备注
:-|:-
MessageAck.`DELIVERED_ACK_TYPE`:0|消息"已接收"，但尚未处理结束。
MessageAck.`STANDARD_ACK_TYPE`:2|"标准"类型，通常表示为消息"处理成功"，broker端可以删除消息了。
MessageAck.`POSION_ACK_TYPE`:1|- 消息"错误"，通常表示"抛弃"此消息。<br>- 比如消息重发多次后，都无法正确处理时，消息将会被删除或者DLQ(死信队列)。
MessageAck.`REDELIVERED_ACK_TYPE`:3|- 消息需"重发"。<br>- 比如consumer处理消息时抛出了异常，broker稍后会重新发送此消息。
MessageAck.`INDIVIDUAL_ACK_TYPE`:4|表示只确认"单条消息"，无论在任何ACK_MODE下。
MessageAck.`UNMATCHED_ACK_TYPE`:5|在Topic中，如果一条消息在转发给“订阅者”时，发现此消息不符合Selector过滤条件，那么此消息将 不会转发给订阅者，消息将会被存储引擎删除(相当于在Broker上确认了消息)。
MessageAck.`EXPIRED_ACK_TYPE`:6|消息过期。

</div>

![](http://dl2.iteye.com/upload/attachment/0094/3092/00602f86-7eb3-32c7-a64d-fc31338116af.jpg)

### 4.1、AUTO_ACKNOWLEDGE下的ACK
> 自动确认。<br>
> 这就意味着消息的确认时机将有consumer择机确认。<br>
> "择机确认"似乎充满了不确定性，这也意味着，开发者必须明确知道"择机确认"的具体时机，否则将有可能导致消息的丢失，或者消息的重复接收。<br>

- 对于consumer而言，optimizeAcknowledge属性只会在AUTO_ACK模式下有效。<br>
- 其中DUPS_ACKNOWLEGE也是一种潜在的AUTO_ACK，只是确认消息的条数和时间上有所不同。
- 在`同步(receive)`方法获取message之后返回之前，会检测`optimizeACK选项`是否开启
    - 如果没有开启，此单条消息将立即确认，所以在这种情况下，message返回之后，如果开发者在处理message过程中出现异常，会导致此消息也不会redelivery，即"潜在的消息丢失"。
    - 如果开启了`optimizeACK`，则会在unAck数量达到`prefetch * 0。65`时确认，当然我们可以指定`prefetchSize = 1`来实现逐条消息确认。
- 在`异步(messageListener)`方式中，将会首先调用`listener.onMessage(message)`，此后再ACK.
    - 如果onMessage方法异常，将导致client端补充发送一个ACK_TYPE为`REDELIVERED_ACK_TYPE`确认指令
    - 如果onMessage方法正常，消息将会正常确认(`STANDARD_ACK_TYPE`)。
    - 此外需要注意，消息的重发次数是有限制的，每条消息中都会包含`redeliveryCounter`计数器，用来表示此消息已经被重发的次数，如果重发次数达到阀值，将会导致发送一个ACK_TYPE为`POSION_ACK_TYPE`确认指令，这就导致broker端认为此消息无法消费，此消息将会被删除或者迁移到"dead letter"通道中。
    - 建议在onMessage方法中使用try-catch，这样可以在处理消息出错时记录一些信息，而不是让consumer不断去重发消息；如果你没有使用try-catch，就有可能会因为异常而导致消息重复接收的问题，需要注意你的onMessage方法中逻辑是否能够兼容对重复消息的判断。

![](http://dl2.iteye.com/upload/attachment/0094/2676/b6499a9e-ee34-3cd5-ae46-eb7002ef1aab.jpg)

### 4.2、CLIENT_ACKNOWLEDGE下的ACK
> 客户端手动确认。<br>
> 这就意味着AcitveMQ将不会“自作主张”的为你ACK任何消息，开发者需要自己择机确认。

在此模式下，开发者需要需要关注几个方法：
1. message.acknowledge()
2. ActiveMQMessageConsumer.acknowledege()
3. ActiveMQSession.acknowledge()
- 其`1`和`3`是等效的，将当前session中<b>所有</b>consumer中尚未ACK的消息都一起确认<br>
- `2`只会对当前consumer中那些尚未确认的消息进行确认

> 开发者可以在合适的时机必须调用一次上述方法。为了避免混乱，对于这种ACK模式下，建议一个session下只有一个consumer。<br>

> 如果开发者忘记调用acknowledge方法，将会导致当consumer重启后，会接受到重复消息，因为对于broker而言，那些尚未真正ACK的消息被视为“未消费”。

> 开发者可以在当前消息处理成功之后，立即调用message.acknowledge()方法来"逐个"确认消息，这样可以尽可能的减少因网络故障而导致消息重发的个数；当然也可以处理多条消息之后，间歇性的调用acknowledge方法来一次确认多条消息，减少ack的次数来提升consumer的效率，不过这仍然是一个利弊权衡的问题。

- 无论是`同步(receive)`/`异步(messageListener)`，ActiveMQ都不会发送`STANDARD_ACK_TYPE`，直到`message.acknowledge()`调用。
- 如果在client端未确认的消息个数达到`prefetchSize * 0.5`时，会补充发送一个ACK_TYPE为`DELIVERED_ACK_TYPE`的确认指令，这会触发broker端可以继续push消息到client端。(参看`PrefetchSubscription.acknwoledge`方法)
- 在broker端，针对每个Consumer，都会保存一个因为`DELIVERED_ACK_TYPE`而`拖延`的消息个数
    - 这个参数为`prefetchExtension`
    - 事实上这个值不会大于`prefetchSize * 0.5`
    - 因为Consumer端会严格控制`DELIVERED_ACK_TYPE`指令发送的时机(参见ActiveMQMessageConsumer.ackLater方法)
- broker端通过`prefetchExtension`与`prefetchSize`互相配合，来决定即将push给client端的消息个数
    - push_count = prefetchExtension + prefetchSize - dispatched.size()
    - dispatched表示已经发送给client端但是还没有`STANDARD_ACK_TYPE`的消息总量
    - 由此可见，在CLIENT_ACK模式下，足够快速的调用acknowledge()方法是决定consumer端消费消息的速率
- <b>如果client端因为某种原因导致acknowledge方法未被执行，将导致大量消息不能被确认，broker端将不会push消息，事实上client端将处于“假死”状态，而无法继续消费消息。我们要求client端在消费1.5*prefetchSize个消息之前，必须acknowledge()一次；通常我们总是每消费一个消息调用一次，这是一种良好的设计。</b>

> <b>额外补充一下：</b><br>
> 1. 所有ACK指令都是依次发送给broker端
> 2. 在CLIET_ACK模式下，消息在交付给listener之前，都会首先创建一个`DELIVERED_ACK_TYPE`的ACK指令，直到client端未确认的消息达到`prefetchSize * 0.5`时才会发送此ACK指令。
> 3. 如果在此之前，开发者调用了acknowledge()方法，会导致消息直接被确认(STANDARD_ACK_TYPE)。
> 4. broker端通常会认为`DELIVERED_ACK_TYPE`确认指令是一种<b>`slow consumer`</b>信号，如果consumer不能及时的对消息进行acknowledge而导致broker端阻塞，那么此consumer将会被标记为`slow`，此后queue中的消息将会转发给其他Consumer。

### 4.3、DUPS_OK_ACKNOWLEDGE下的ACK
> 消息可重复确认。此模式下，可能会出现重复消息。<br>
> 并不是一条消息需要发送多次ACK才行。<br>
> 它是一种潜在的"`AUTO_ACK`"确认机制，为批量确认而生，而且具有“延迟”确认的特点。<br>
> 对于开发者而言，这种模式下的代码结构和`AUTO_ACKNOWLEDGE`一样，不需要像`CLIENT_ACKNOWLEDGE`那样调用acknowledge()方法来确认消息。<br>

1. Destination是Queue通道
    - 可以认为`DUPS_OK_ACK`就是“`AUTO_ACK` + `optimizeACK` + `(prefetch > 0)`”这种情况，在确认时机上几乎完全一致；
    - 此外在此模式下，如果`prefetchSize =1`或者`没有开启optimizeACK`，也会导致消息逐条确认，从而<b>失去</b>批量确认的特性。
2. Destination为Topic
    - `DUPS_OK_ACKNOWLEDGE`才会产生JMS规范中诠释的意义。
    - 即无论optimizeACK是否开启，都会在消费的消息个数>=`prefetch * 0.5`时，批量确认(STANDARD_ACK_TYPE)
    - 在此过程中，不会发送`DELIVERED_ACK_TYPE`的确认指令，这是`1`和AUTO_ACK的最大的区别
-  这也意味着，当consumer故障重启后，那些尚未ACK的消息会重新发送过来。

### 4.4、SESSION_TRANSACTED下的ACK

> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>