[toc]

## Zookeeper开源客户端ZkClient的使用

### 1、创建连接、创建节点、修改节点、删除节点
#### 1.1、创建连接
``` java
public ZkClient(String zkServers)
public ZkClient(String zkServers, int connectionTimeout)
public ZkClient(String zkServers, int sessionTimeout, int connectionTimeout)
public ZkClient(String zkServers, int sessionTimeout, int connectionTimeout, ZkSerializer zkSerializer)
public ZkClient(IZkConnection connection)
public ZkClient(IZkConnection connection, int connectionTimeout)
public ZkClient(IZkConnection connection, int connectionTimeout, ZkSerializer zkSerializer)
```
``` java
public ZkConnection(String zkServers)
public ZkConnection(String zkServers, int sessionTimeOut)
```
<div class="xych-table" style="font-size:14px;">

参数|含义
:-|:-
zkServers|Zookeeper服务器的地址和端口号`ip:port`。集群环境使用逗号隔开。
sessionTimeout|会话超时时间。
connectionTimeout|连接时间
</div>