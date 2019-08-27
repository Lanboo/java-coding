[toc]

# Kafka消息消费_分配分区简介

可以参考：[Kafka分区分配策略（4）——分配的实施](https://blog.csdn.net/u013256816/article/details/81123907)

## 1、名词介绍
- Kafka角色：`Coordinator`
    > kafka提供了Coordinator的角色，用来管理Consumer Group以及分区分配。

    > 当`Consumer Group`中的第一个消费者启动时，它回去和kafka集群确定谁是<b>它们组的</b>Coordinator

- `Consumer Leader`
    > 一个消费组的leader，负责分配partition。

- `GroupCoordinatorRequest`（GCR）
- `JoinGroupRequest`（JGR）
- `SyncGroupRequest`（SGR）
## 2、主要流程(简要)
1. 发送GCR寻找Coordinator
    > Consumer向Kafka集群发送GCR请求，Kafka集群返回负载最小的Broker作为该Group的Coordinator，该消费者尝试连接Coordinate。
2. 发送JGR请求加入该分组
    > 当某个消费者找到Coordinator后，就会发起加入Group的请求<br>
    > Coordinator在接收到消费者的JGR请求后，从Group中选举出Consumer Leader，让其进行分区分配。并把相应信息（Consumer Leader、分区策略等）封装成Response返回给各个消费者。
3. 发送SGR请求
    > 在消费者发送JGR请求并得到Response，如果某个消费者发现自己是Consumer Leader，那么该消费者会进行分区分配，并把分配结果封装在SGR中，之后发起SGR请求；如果自己不是Consumer Leader，也会发起SGR，只不过，该SGR中不会有分区分配结果。<br>

    > 在Coordinator接收到Consumer Leader的SGR请求，将分区分配结果返回给所有消费者
4. 消费者根据分配结果，执行相应操作

## 3、主要流程
- 对于每个consumer group子集，都会在服务端对应一个GroupCoordinator进行管理。<br>GroupCoordinator会在zookeeper上添加watcher，当消费者加入或者退出consumer group时，会修改zookeeper上保存的数据，从而触发GroupCoordinator开始Rebalance操作
<br><br>
- 当消费者准备加入某个Consumer group或者GroupCoordinator发生故障转移时，消费者并不知道GroupCoordinator的在网络中的位置，这个时候就需要确定GroupCoordinator，消费者会向集群中的任意一个Broker节点发送ConsumerMetadataRequest请求，收到请求的broker会返回一个response作为响应，其中包含管理当前ConsumerGroup的GroupCoordinator
<br><br>
- 消费者会根据broker的返回信息，连接到groupCoordinator，并且发送HeartbeatRequest，发送心跳的目的是要要奥噶苏GroupCoordinator这个消费者是正常在线的。当消费者在指定时间内没有发送心跳请求，则GroupCoordinator会触发Rebalance操作。
<br><br>
- 发起join group请求，两种情况
    - 如果GroupCoordinator返回的心跳包数据包含异常，说明GroupCoordinator因为前面说的几种情况导致了Rebalance操作，那这个时候，consumer会发起join group请求
    - 新加入到consumer group的consumer确定好了GroupCoordinator以后
<br><br>
- 消费者会向GroupCoordinator发起join group请求，GroupCoordinator会收集全部消费者信息之后，来确认可用的消费者，并从中选取一个消费者成为group_leader。并把相应的信息（分区分配策略、leader_id、…）封装成response返回给所有消费者，但是只有group leader会收到当前consumer group中的所有消费者信息。当消费者确定自己是group leader以后，会根据消费者的信息以及选定分区分配策略进行分区分配
<br><br>
- 接着进入Synchronizing Group State阶段，每个消费者会发送SyncGroupRequest请求到GroupCoordinator，但是只有Group Leader的请求会存在分区分配结果，GroupCoordinator会根据Group Leader的分区分配结果形成SyncGroupResponse返回给所有的Consumer。


### 3.1、分区策略的选定
> 由于消费者可以指定分区策略，当消费者指定的分区策略不一致时，就需要选择一个分区策略，用于分区。

可以参考：[Kafka分区分配策略（4）——分配的实施](https://blog.csdn.net/u013256816/article/details/81123907)

