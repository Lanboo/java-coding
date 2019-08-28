[toc]

# Kafka分区副本机制

> ![](../etc/kafka_分区副本示意图.png)<br>
> 如图，该Topic下有4个分区：TP1、TP2、TP3、TP4，每个分区有3个副本，其中一个副本的角色是Leader。

- 分区副本的创建？
- 副本Leader的选举？
- 副本之间的数据同步？
- 那些数据允许被消费者消费？

## 1、副本相关命令
- 创建Topic:test,同时指定分区数为3，副本数3
    ``` shell
    > ./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic test --partitions 3 --replication-factor 3
    ```

    > 副本数不能大于kafka集群的节点数
- 查看某个Topic的信息
    ``` shell
    > ./kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic test
    Topic:test	PartitionCount:3	ReplicationFactor:3	Configs:segment.bytes=1048576,retention.bytes=1048576
        Topic: test	Partition: 0	Leader: 0	Replicas: 0,1,2	Isr: 0,2,1
        Topic: test	Partition: 1	Leader: 2	Replicas: 2,0,1	Isr: 2,0,1
        Topic: test	Partition: 2	Leader: 1	Replicas: 1,2,0	Isr: 1,2,0
    ```
- 动态增加某个Topic的副本数<br>
    [百度：kafka 增加副本](https://www.baidu.com/s?wd=kafka%20%E5%A2%9E%E5%8A%A0%E5%89%AF%E6%9C%AC&rsv_spt=1&rsv_iqid=0x963caebb0000437b&issp=1&f=8&rsv_bp=1&rsv_idx=2&ie=utf-8&rqlang=cn&tn=baiduhome_pg&rsv_enter=1&rsv_dl=tb&inputT=21731&rsv_t=fe9aQyndbfCqzUt%2FaIrxYh5UUfzHbjUIlUJohBzfQvsjMIdsVE59Uh7b43CogOJe9wQD&oq=kafka%2520offset&rsv_pq=f15b955400066f22&rsv_sug3=99&rsv_sug1=63&rsv_sug7=100&rsv_sug2=0&rsv_sug4=22630)

## 2、副本Leader的选举
<b>PS：与生产者、消费者交互的只有Leader，其他副本只负责存储数据。</b>

``` shell
# 查看某个Topic的信息
> ./kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic test
Topic:test	PartitionCount:3	ReplicationFactor:3	Configs:segment.bytes=1048576,retention.bytes=1048576
    Topic: test   Partition: 0    Leader: 0            Replicas: 0,1,2      Isr: 0,2,1
    Topic: test   Partition: 1    Leader: 2            Replicas: 2,0,1      Isr: 2,0,1
    Topic: test   Partition: 2    Leader: 1            Replicas: 1,2,0      Isr: 1,2,0
#                 分区编号         分区所在broker.id    副本所在的broker.id   ISR集合
```

- ISR集合
    1. 整个副本集合的一个子集。<br>
    2. ISR中的每个节点都是优质的副本：可用，且消息数量与Leader副本相差不多。
    3. 当Leader宕机后，直接从ISR中选取一个当做新的Leader即可。（往往选取ISR集合中第一顺位的节点当做Leader）

根据上面的第3条，副本选举的实现，就集中在ISR集合是怎么维护的了。

- ISR集合的维护
    1. 可用
    2. 副本最后一条消息的offset与Leader最后一条消息的offset的时间差值不能超过指定的阈值：[replica.lag.time.max.ms](http://kafka.apache.org/documentation/#brokerconfigs)，否则该副本会被踢出ISR集合。
    3. 被踢出ISR集合的副本，当满足2之后，则重新加入ISR集合。
<br><br>
- 所有副本均不可用，怎么选举Leader？
    1. 等待ISR中的任一个Replica“活”过来，并且选它作为Leader
    2. 选择第一个“活”过来的Replica（不一定是ISR中的）作为Leader
    - 选择1，那不可用的时间就可能会相对较长
    - 选择2，那即使它并不保证已经包含了所有已commit的消息，它也会成为Leader而作为consumer的数据源。从而造成数据丢失。

    - `unclean.leader.election.enable`：默认值：false。
        - Indicates whether to enable replicas not in the ISR set to be elected as leader as a last resort, even though doing so may result in data loss.
        - 指示是否允许不在ISR集中的副本被选为最终的领导者，即使这样做可能会导致数据丢失。
