[toc]

# 阿里云安装Kafka遇到的坑

## 1、公网IP连接不了Kafka

- 防火墙已关：[Centos防火墙设置与端口开放的方法](https://blog.csdn.net/u011846257/article/details/54707864)
- 阿里云主机名称：`lanboo`
    - 可以查看`vim /etc/hostname`查看
- `/etc/hosts`配置
    ``` hosts
    127.0.0.1 localhost lanboo 
    ```
- `Kafka`配置：`config/server.properties`
    ``` properties
    broker.id=0
    # 配置本机的内网IP
    listeners=PLAINTEXT://:9092
    # 配置外网IP
    advertised.listeners=PLAINTEXT://xych.online:9092
    zookeeper.connect=localhost:2181
    ```
    - `listeners`，<b>需要填写内网IP</b>
        > 未配置IP的情况下，以`java.net.InetAddress.getCanonicalHostName()`获取主机名，即`lanboo`，所以需要在`/etc/hosts`配置`127.0.0.1`和`lanboo`的映射。
    - `advertised.listeners`，外网访问
