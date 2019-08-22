[toc]

# Kafka消息消费_分区分配策略简介
> Kafka怎么把某个Topic的Partition分配给消费线程的？

## 1、简介
- `group.id`
    > `Consumer Group`是Kafka提供的可扩展且具有容错性的消费机制。

    > 具体参照：[group.id](kafka_04_Kafka参数简介.md#21groupid)

    ![](../etc/Consumer端group.id参数图解.png)

- `Topic`、`Partition`
    > 在kafka中，`Topic`是一个存储消息的逻辑概念，可以认为是一个消息集合。

    > `Partition`，分区。<br>
    > 每个`Topic`可以划分多个`Partition`（每个Topic至少一个Partition）。

    > 具体参照：[Topic](kafka_06_Kafka_Topic、Partition、分区副本.md#1topic)、[Partition](kafka_06_Kafka_Topic、Partition、分区副本.md#2partition)

    ![](../etc/kafka_partition示意图.png)

## 2、Kafka分区分配
> 这里只讨论，<b>同一个`Topic`相同`group.id`下，多个消费线程多个分区</b>

- <b>一个分区同一时间只会被一个消费线程所消费；</b>
- <b>一个消费线程可以同时消费多个分区。</b>
- Kafka只保证在一个分区内消息是有序的，但是一个消费线程消费多个分区时，无法保证整体的有序性。

## 2.1、Kafka分区分配时机
- 同一个`Consumer Group`中，新增了消费线程
- 消费线程离开了当前`Consumer Group`，例如消费线程主动停机或者宕机。
- 当前topic新增了分区。
以上都会触发重新分配分区，即`rebalance`

## 2.2、多个分区多个消费线程
<div style = "font-size:14px;">

数量对比|结果
:-|:-
`分区数` = `消费线程数`|一个消费线程消费一个分区即可。
`分区数` < `消费线程数`|有（`消费线程数 - 分区数`）个消费线程是空闲的。
`分区数` > `消费线程数`|- 按照消费端指定的分配策略来分配。<br>- 参照下面。
</div>
## 3、分区分配策略
<div style = "font-size:14px;">

分配策略|说明
:-|:-
`RangeAssignor`|范围。Kafka的默认策略
`RoundRobinAssignor`|轮询
`StickyAssignor`|粘性
</div>

``` java
// org.apache.kafka.clients.consumer.ConsumerConfig
public static final String PARTITION_ASSIGNMENT_STRATEGY_CONFIG = "partition.assignment.strategy";
```

``` java 
Properties properties = new Properties();
...
properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, RangeAssignor.class.getName());
this.kafkaConsumer = new KafkaConsumer<>(properties);
```

### 3.1、`RangeAssignor`
> n = 分区数 / 消费线程数<br>
> m = 分区数 % 消费线程数

> 那前m个消费线程将消费n+1个分区，剩下的消费线程将消费n个分区。

### 3.2、`RoundRobinAssignor`
> 轮询分区策略是把所有partition和所有consumer线程都列出来，然后按照hashcode进行排序。最后通过轮询算法分配partition给消费线程。

### 3.3、`StickyAssignor`
> kafka在0.11.x版本支持了StrickyAssignor, 翻译过来叫粘滞策略，它主要有两个目的
- 分区的分配尽可能的均匀
- 分区的分配尽可能和上次分配保持相同
> 当两者发生冲突时， 第 一 个目标优先于第二个目标。


