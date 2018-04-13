[toc]

## 初步认识[zookeeper](zookeeper.apache.org)

### 1、zookeeper是什么？
> 分布式数据一致性的解决方案。

> ZooKeeper is a centralized service for maintaining configuration information, naming, providing distributed synchronization, and providing group services. All of these kinds of services are used in some form or another by distributed applications. Each time they are implemented there is a lot of work that goes into fixing the bugs and race conditions that are inevitable. Because of the difficulty of implementing these kinds of services, applications initially usually skimp on them ,which make them brittle in the presence of change and difficult to manage. Even when done correctly, different implementations of these services lead to management complexity when the applications are deployed.<br>
> ZooKeeper是一个集中服务，用于维护配置信息，命名，提供分布式同步和提供组服务。所有这些类型的服务都以某种形式被分布式应用程序使用。每次他们实施时，都会有很多工作来解决不可避免的错误和竞争条件。由于实施这些服务的困难，最初的应用程序通常会吝啬，这使得它们在变化和难以管理的情况下变得脆弱。即使正确完成，这些服务的不同实现会导致部署应用程序时的管理复杂性。

[ZooKeeper官网](zookeeper.apache.org)

### 2、zookeeper能做什么？
数据的发布/订阅（配置中心:disconf）  、 负载均衡（dubbo利用了zookeeper机制实现负载均衡） 、命名服务、
master选举(kafka、hadoop、hbase)、分布式队列、分布式锁


### 3、zookeeper的特性
> 顺序一致性：从同一个客户端发起的事务请求，最终会严格按照顺序被应用到zookeeper中。<br>
> 原子性：所有的事务请求的处理结果在整个集群中的所有机器上的应用情况是一致的，也就是说，要么整个集群中的所有机器都成功应用了某一事务，要么全都不应用。<br>
> 可靠性：一旦服务器成功应用了某一个事务数据，并且对客户端做了响应，那么这个数据在整个集群中一定是同步并且保留下来的
> 实时性：一旦一个事务被成功应用，客户端就能够立即从服务器端读取到事务变更后的最新数据状态；（zookeeper仅仅保证在一定时间内，近实时）



### 4、zookeeper的安装
#### 4.1、下载
[官网](https://zookeeper.apache.org/)

[下载地址](http://apache.fayea.com/zookeeper/)

```
//如下是版本修饰词的含义
Snapshot: 版本代表不稳定、尚处于开发中的版本
Alpha: 内部版本
Beta: 测试版
Demo: 演示版
Enhance: 增强版
Free: 自由版
Full Version: 完整版，即正式版
LTS: 长期维护版本
Release: 发行版
RC: 即将作为正式版发布
Standard: 标准版
Ultimate: 旗舰版
Upgrade: 升级版
```
本人下载的是zookeeper-3.4.11.tar.gz

#### 4.2、单机版安装
1. 解压到某目录下即可。<br>
    `tar -zxvf zookeeper-3.4.11.tar.gz`
2. 打开conf文件夹
3. 拷贝一份`zoo_sample.cfg`，重命名`zoo.cfg`<br>
    `cp zoo_sample.cfg zoo.cfg`
4. 在`bin`目录下，启动`zkServer.sh`<br>
    `sh zkServer.sh`
5. 启动客户端`zkCli.sh`，连接zookeeper<br>
`sh zkCli.sh -server  ip:port`   //其中ip是服务器ip，端口号在zoo.cfg中有配置，默认2181

#### 4.3、集群版
##### 4.3.1、集群的角色
> `leader`：负责接收客户端的写请求，将数据同步到`follower`和`observer`<br>
> `follower`：负责客户端的读请求，当`leader`不可用时，在剩余的`follower`中投票选取出一个新的`leader`<br>
> `observer`：只负责客户端的读请求；不参与投票，只接受投票结果。

> 另外：<br>
> `leader`接收写请求，会同步到`follower`和`observer`，但是当同步数据到`follower`数量的一半，就会告知客户端写请求成功。<br>
> `leader`是被投票选举出来的不是配置的，`observer`是需要在配置中特别声明出来。
> `observer`的配置文件中需要加上`peerType=observer`
##### 4.3.1、配置
1. 首先，在<b>每个</b>节点的`zoo.cfg`文件中添加`server.id=ip:port:port`
```
// server.id=ip:port:port
server.1=192.168.27.128:2828:3131
server.2=192.168.27.129:2828:3131
server.3=192.168.27.130:2828:3131
server.4=192.168.27.131:2828:3131:observer
```

> id的范围 1-255，用id来标识该机器在集群中的机器序号<br>
> 如上配置，代表4个节点组成的集群，其中id=4的节点是observer角色<br>
> 第一个port，是leader与follower同步数据时使用的端口号<br>
> 第二个port，当leader不可用时，进行投票<br>
> 另外，客户端与集群连接的默认端口号是2181

2. <b>每个</b>节点的在`zoo.cfg`中，有个`dataDir`属性，其默认值`/tmp/zookeeper`，在其目录下创建`myid`文件，其文件内容就一行数据，数据内容就是id的值。
3. 启动<b>每个</b>节点的服务