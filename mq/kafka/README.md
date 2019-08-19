[toc]

## Kafka

### 1、Kafka安装&简单操作
[Kafka安装&简单操作](doc/kafka_01_安装&简单操作.md)

### 2、阿里云安装Kafka遇到的坑
[阿里云安装Kafka遇到的坑](doc/kafka_02_阿里云安装Kafka遇到的坑.md)

### 3、Kafka简单示例
[Kafka简单示例](doc/kafka_03_Kafka简单示例.md)

### 4、Kafka参数简介
[Kafka参数简介](doc/kafka_04_Kafka参数简介.md)

- `Producer`端参数
    - [`acks`](doc/kafka_04_Kafka参数简介.md#11acks)
    - [`batch.size`](doc/kafka_04_Kafka参数简介.md#12batchsize)
    - [`linger.ms`](doc/kafka_04_Kafka参数简介.md#13lingerms)
    - [`max.request.size`](doc/kafka_04_Kafka参数简介.md#14maxrequestsize)
- `Consumer`端参数
    - [`group.id`](doc/kafka_04_Kafka参数简介.md#21groupid)
    - [`enable.auto.commit`、`auto.commit.interval.ms`](doc/kafka_04_Kafka参数简介.md#22enableautocommitautocommitintervalms)
    - [`auto.offset.reset`](doc/kafka_04_Kafka参数简介.md#23autooffsetreset)
    - [`max.poll.records`、`max.poll.interval.ms`](doc/kafka_04_Kafka参数简介.md#24maxpollrecordsmaxpollintervalms)