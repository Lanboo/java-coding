# ActiveMQ事务&消息确认方式&持久化

## 1、事务&消息确认（ACK）方式
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
2|acknowledgeMode|消息确认模式|Session.`SESSION_TRANSACTED`(0)：事务<br>Session.`AUTO_ACKNOWLEDGE`(1)：自动应答<br>Session.`CLIENT_ACKNOWLEDGE`(2)：手动应答<br>Session.`DUPS_OK_ACKNOWLEDGE`(3)：自动批量确认<br>ActiveMQSession.`INDIVIDUAL_ACKNOWLEDGE`(4)：单条消息确认


transacted|acknowledgeMode|备注
:-|:-|:-
true |Session.`SESSION_TRANSACTED`：(0)|-
false|Session.`AUTO_ACKNOWLEDGE`：(1)|-
false|Session.`CLIENT_ACKNOWLEDGE`：(2)|-
false|Session.`DUPS_OK_ACKNOWLEDGE`：(3)|-
false|ActiveMQSession.`INDIVIDUAL_ACKNOWLEDGE`：(4)|AcitveMQ补充了一个自定义的ACK模式

</div>

- ACK模式描述了Consumer与broker确认消息的方式(时机)，比如当消息被Consumer接收之后，Consumer将在何时确认消息。
- 对于broker而言，只有接收到ACK指令，才会认为消息被正确的接收或者处理成功了，通过ACK，可以在consumer（/producer）与Broker之间建立一种简单的“担保”机制。

详见[ActiveMQ消息传送机制以及ACK机制详解](activemq_08_ActiveMQ消息传送机制以及ACK机制详解.md)

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






