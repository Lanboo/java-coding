[toc]

# Kafka安装&简单操作
## 1、单机安装
[Apache Kafka Download](http://kafka.apache.org/downloads) - Binary downloads
- 下载 & 解压
    ``` shell
    cd kafka
    wget https://www.apache.org/dyn/closer.cgi?path=/kafka/2.3.0/kafka_2.11-2.3.0.tgz
    tar -zxvf kafka_2.11-2.3.0.tgz
    ```
- Zookeeper依赖
    ``` shell
    vim kafka/kafka_2.12-2.3.0/config/server.properties

    # Zookeeper connection string (see zookeeper docs for details).
    # This is a comma separated host:port pairs, each corresponding to a zk
    # server. e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002".
    # You can also append an optional chroot string to the urls to specify the
    # root directory for all kafka znodes.
    zookeeper.connect=localhost:2181
    ```
- 启动Kafka
    ``` shell
    cd kafka/kafka_2.12-2.3.0/bin
    sh kafka-server-start.sh ../config/server.properties

    # 后台线程启动
    sh kafka-server-start.sh -daemon ../config/server.properties

    # 停止Kafka
    sh kafka-server-stop.sh
    ```

## 2、简单操作
参考官方文档：[Quick Start](http://kafka.apache.org/documentation/#quickstart)

安利一个Zookeeper可视化工具：[百度云](https://pan.baidu.com/s/1wJJ5c1CKJi-FUd1Z_IbX1Q)<br>
PS：节点较多时，连接会失败，学习时使用即可。

- 创建一个名为`"test"`的`Topic`
    ``` shell
    sh kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic test

    # 查看所有的Topic
    sh kafka-topics.sh --list --bootstrap-server localhost:9092
    ```
- 发送消息
    > PS：
- 接收消息


## 3、集群安装