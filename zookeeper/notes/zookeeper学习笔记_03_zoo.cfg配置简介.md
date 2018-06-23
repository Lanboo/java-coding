[toc]

## zookeeper配置文件zoo.cfg简介

### `tickTime=2000`

> zookeeper中最小的时间单位长度 （ms）。<br>
> 下面配置中出现的时间相关的数据都是以这项配置为单位的。

### `initLimit=10`
> `follower`节点启动后与`leader`节点完成数据同步的时间。<br>
> 实际时间 = `10 * tickTime`

### `syncLimit=5`
> `leader`节点和`follower`节点进行心跳检测的最大延时时间<br>
> 实际时间 = `5 * tickTime`

### `dataDir=/tmp/zookeeper`
> 表示zookeeper服务器存储快照文件的目录

#### `dataLogDir`
> 表示配置 zookeeper事务日志的存储路径<br>
> 默认指定在`dataDir`目录下

### `clientPort=2181`
> 表示客户端和服务端建立连接的端口号<br>
> 默认2181<br>

### 会话时间
关于会话时间的作用和理解，请看[ZooKeeper 会话超时](https://blog.csdn.net/kobejayandy/article/details/26289273)
> 当客户端与Zookeeper服务器创建连接时，会话随着创建，生成一个全局唯一的会话ID（Session Id） <br>
> 客户端一般会指定一个会话超时时间，当这个时间不在`minSessionTimeout`和`maxSessionTimeout`之间，会被强制设为最大或最小时间。<br>
> 服务端在这个超时时间内检测客户端是否正常连接(客户端会定时向服务器发送heart_beat,服务器会重置下次的SESSTION_TIME)<br>
> 当服务器在这个超时时间内未收到客户端的heart_beat，认为客户端的会话断开<br>
> Zookeeper中与会话绑定的数据（比如临时节点、Watcher等）都是随着会话的失效而被清除的。<br>
#### `minSessionTimeout`
> 默认是2，即`2 * tickTime`
#### `maxSessionTimeout`
> 默认是20，即`20 * tickTime`



