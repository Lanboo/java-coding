# ActiveMQ事务&消息确认方式&持久化

## 1、事务&消息确认方式
> javax.jms.Connection#createSession(boolean transacted, int acknowledgeMode)
``` java
package javax.jms;

public interface Connection {

    Session createSession(boolean transacted, int acknowledgeMode) throws JMSException;

    ...
}
```

<div style = "font-size:13px;">

序号|参数|含义|取值
:-:|:-|:-|:-
1|transacted|是否开启事务|`true`：开启事务<br>`false`：不开启事务
2|acknowledgeMode|消息确认模式|Session.`SESSION_TRANSACTED`(0)：事务<br>Session.`AUTO_ACKNOWLEDGE`(1)：自动应答<br>Session.`CLIENT_ACKNOWLEDGE`(2)：手动应答<br>Session.`DUPS_OK_ACKNOWLEDGE`(3)：延迟提交


transacted|acknowledgeMode
:-|:-
true |Session.`SESSION_TRANSACTED`：(0)
false|Session.`AUTO_ACKNOWLEDGE`：(1)
false|Session.`CLIENT_ACKNOWLEDGE`：(2)
false|Session.`DUPS_OK_ACKNOWLEDGE`：(3)

acknowledgeMode|生产者|消费者
:-|:-|:-
Session.`SESSION_TRANSACTED`|transacted = true;<br>commit时，事务提交，消息被队列记录|commit时，事务提交，消息被确认
Session.`AUTO_ACKNOWLEDGE`|自动确认|接收到消息就认为消费成功，直接从队列中移除
Session.`CLIENT_ACKNOWLEDGE`|自动确认|客戶端调用acknowledge方法手动签收
Session.`DUPS_OK_ACKNOWLEDGE`|自动确认|- 不是必须签收，消息可能会重复发送。<br>- 客户端需要进行消息的重复处理控制。

</div>

## 2、持久化、非持久化
> javax.jms.MessageProducer.setDeliveryMode(int deliveryMode)<br>
> javax.jms.MessageProducer.send(Message message)<br>
> javax.jms.MessageProducer.send(Message message, int deliveryMode, int priority, long timeToLive)

- deliveryMode 取值
<div style = "font-size:13px;">

deliveryMode取值|含义
:-|:-
DeliveryMode.PERSISTENT|持久化，默认
DeliveryMode.NON_PERSISTENT|非持久化
</div>






