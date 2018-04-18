[toc]

## zookeeper配置文件zoo.cfg简介

#### `tickTime=2000`
> zookeeper中最小的时间单位长度 （ms）。<br>
> 下面配置中出现的时间相关的数据都是以这项配置为单位的。

#### `initLimit=10`
> `follower`节点启动后与`leader`节点完成数据同步的时间。<br>
> 实际时间 = `10 * tickTime`

#### `syncLimit=5`
> `leader`节点和`follower`节点进行心跳检测的最大延时时间<br>
> 实际时间 = `5 * tickTime`

#### `dataDir=/tmp/zookeeper`
> 表示zookeeper服务器存储快照文件的目录

##### `dataLogDir`
> 表示配置 zookeeper事务日志的存储路径<br>
> 默认指定在`dataDir`目录下

#### `clientPort=2181`
> 表示客户端和服务端建立连接的端口号<br>
> 默认2181<br>



