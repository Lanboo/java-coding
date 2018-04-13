[toc]

## 分布式简介

#### 1、分布式环境的特点
> 分布性、并发性、无序性

> 分布性：指分布式系统在物理空间的不连续性。<br>
> 并发性：不论单机还是集群，程序的并发操作都是很常见的。比如单机下的多线程编程；集群下多个节点访问同一节点的资源。<br>
> 无序性：因网络传输的不可靠性，你无法保证，接受节点接受消息的顺序和发送顺序保存一致。<br>

#### 2、分布式面临的问题

> 网络通信：网络本身的不可靠性，会涉及到一些通信问题。

> 网络分区（脑裂）：当网络发生异常导致分布式系统中部分节点的网络延迟不断加大，导致不能正常通信。

> 三态：成功、失败、超时。

> 分布式事务

#### 3、中心化和去中心化
    
> 去中心化，不是不要中心，而是由节点来自由选择中心、自由决定中心。简单地说，中心化的意思，是中心决定节点。节点必须依赖中  心，节点离开了中心就无法生存。在去中心化系统中，任何人都是一个节点，任何人也都可以成为一个中心。任何中心都不是永久的，而是阶段性的，任何中心对节点都不具有强制性。[^百度百科]

[^百度百科]:[百度百科](https://baike.baidu.com/item/%E5%8E%BB%E4%B8%AD%E5%BF%83%E5%8C%96/8719532?fr=aladdin)

在zookeeper中，当集群中处于`leader`角色的节点出现问题时，马上又剩余的节点<b>`选举`</b>出新的`leader`节点。

#### 4、CAP和BASE理论

可以参考：[CAP原理和BASE思想](http://www.jdon.com/37625)

##### 4.1、CAP
> [Consistency](https://translate.google.cn/#auto/zh-CN/Consistency)一致性：所有节点上的数据，时刻保持一致。<br>
> [Availability](https://translate.google.cn/#auto/zh-CN/Availability)可用性：每个请求都能收到相应，无论成功还是失败。即好的相应能力。<br>
> [Partition-tolerance](https://translate.google.cn/#auto/zh-CN/Partition-tolerance)分区容错：当出现网路分区（脑裂）后，保证系统的正常运行。即可靠性。

> <b>定理：任何分布式系统只可同时满足二点，没法三者兼顾。</b><br>
> 忠告：不要将精力浪费在如何设计能满足三者的完美分布式系统，而是应该进行取舍。

##### 4.2、BASE

4.2.1、ACID模型
> [Atomicity](https://translate.google.cn/#auto/zh-CN/Atomicity)原子性：一个事务中所有操作都必须全部完成，要么全部不完成。<br>
> [Consistency](https://translate.google.cn/#auto/zh-CN/Consistency)一致性：在事务开始或结束时，数据库应该在一致状态。<br>
> [Isolation](https://translate.google.cn/#auto/zh-CN/Isolation)隔离性：事务将假定只有它自己在操作数据库，彼此不知晓。<br>
> [Durability](https://translate.google.cn/#auto/zh-CN/Durability)持久性：一旦事务完成，就不能返回。

4.2.2、BASE
BASE模型反ACID模型，完全不同ACID模型，牺牲高一致性，获得可用性或可靠性。
> [<b>B</b>asically <b>A</b>vailable](https://translate.google.cn/#auto/zh-CN/Basically%20Available)基本可用。<br>
> [<b>S</b>oft state](https://translate.google.cn/#auto/zh-CN/Soft%20state)软状态：状态可以有一段时间不同步，异步。<br>
> [<b>E</b>ventually consistent](https://translate.google.cn/#auto/zh-CN/Eventually%20consistent)最终一致：最终数据是一致的就可以了，而不是时时高一致。



<br><br><br><br><br><br>学自咕泡学院