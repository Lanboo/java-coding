[toc]

# 延迟队列
- 场景描述：
    1. 智能家居，热水器15分钟后开始工作
    2. 未付款的订单，15分钟后自动关闭
    3. 支付结果通知商户，通知失败后梯度通知

- 实现方式
    1. 数据库存储，定时器扫描
    2. RabbitMQ，消息TTL + 死信队列
    3. rabbitmq-delayed-message-exchange 插件
    4. 其他


## 死信队列方式实现
> 智能家居，热水器15分钟后开始工作<br>
> 未付款的订单，15分钟后自动关闭<br>
> ![](../etc/RabbitMQ_延迟队列1.png)

> 支付结果通知商户，通知失败后梯度通知：<br>
> ![](../etc/RabbitMQ_延迟队列2.png)

缺点：
对于梯度延迟队列时，梯度越多，绑定关系越多。


## rabbitmq-delayed-message-exchange 插件

> 目前只支持：Linux

[官方插件](https://www.rabbitmq.com/community-plugins.html)

[3.7.x](https://dl.bintray.com/rabbitmq/community-plugins/3.7.x/rabbitmq_delayed_message_exchange/rabbitmq_delayed_message_exchange-20171201-3.7.x.zip)

``` shell
cd rabbitmq_server-3.7.17/plugins
wget https://dl.bintray.com/rabbitmq/community-plugins/3.7.x/rabbitmq_delayed_message_exchange/rabbitmq_delayed_message_exchange-20171201-3.7.x.zip

```