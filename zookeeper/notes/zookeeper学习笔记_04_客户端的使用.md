[toc]

## zookeeper中的一些概念、客户端的使用

### 1、一些概念
#### 1.1、zookeeper的数据模型
> zookeeper的数据模型和文件系统类似。<br>
> 每一个节点称为`znode`。<br>
> `znode`是zookeeper中的最小数据单元。<br>
> 每一个znode上都可以`保存数据`和`挂载子节点`。<br>
> 从而构成一个层次化的属性结构节点特性<br>

#### 1.2、持久化节点、持久化有序节点
> `持久化节点`：节点创建后会一直存在zookeeper服务器上，直到主动删除<br>
> `持久有序化节点`：每个节点都会为它的一级子节点维护一个顺序。<br>

#### 1.3、临时节点、临时有序节点
> 临时节点的生命周期和客户端的会话保持一致。当客户端会话失效，该节点自动清理<br>
> 在临时节点上增加一个顺序性特性<br>

> 临时节点不允许创建子节点，即临时节点就是叶子节点。

#### 1.4、会话
<pre>
1. NOT_CONNECTION  (未连接)
2. CONNECTING      (正在连接)
3. CONNECTED       (已连接)
4. CLOSED          (断开连接)
</pre>

#### 1.5、Watcher
> zookeeper提供了分布式数据发布/订阅,zookeeper允许客户端向服务器注册一个Watcher监听。当服务器端的节点触发指定事件的时候会触发Watcher。服务端会向客户端发送一个事件通知。<br>
> <b>Watcher的通知是一次性，一旦触发一次通知后，该Watcher就失效。</b>

#### 1.6、ACL
> zookeeper提供控制节点访问权限的功能，用于有效的保证zookeeper中数据的安全性。<br>
> 避免误操作而导致系统出现重大事故。<br>

> `ACL`在表示为`scheme:id:permissions`，`scheme`表示采用哪一种机制，`id`表示谁，`permissions`表示具有什么权限。

##### 1.6.1、zookeeper提供了如下几种机制（scheme）
- <b>world</b>：它下面只有一个id, 叫anyone，即`world:anynoe:<permissions>`，表示zookeeper中对所有人有权限的结点就是属于world:anyone的。
- <b>auth</b>：这种机制不需要`id`，只要是通过authentication检查的user都有权限
- <b>digest</b>：它对应的id为username:BASE64(SHA1(password))，它需要先通过username:password形式的authentication
- <b>ip</b>：它对应的id为客户机的IP地址，设置的时候可以设置一个ip段，比如ip:192.168.1.0/16, 表示匹配前16个bit的IP段
- <b>super</b>：在这种scheme情况下，对应的id拥有超级权限，可以做任何事情(cdrwa)

##### 1.6.2、zookeeper提供了如下几种permission
- <b>CREATE(c)</b>：创建权限，可以在在当前node下创建child node
- <b>DELETE(d)</b>：删除权限，可以删除当前的node
- <b>READ(r)</b>：读权限，可以获取当前node的数据，可以list当前node所有的child nodes
- <b>WRITE(w)</b>：写权限，可以向当前node写数据
- <b>ADMIN(a)</b>：管理权限，可以设置当前node的permission
```
// 例如
setAcl /zookeeper/node1 world:anyone:cdrw
```
参考：<br>
[zookeeper ACL使用](https://www.cnblogs.com/xuxiuxiu/p/6306825.html)<br>
如果想自己实现一种scheme机制，可以看看[说说Zookeeper中的ACL](https://www.cnblogs.com/xsht/p/5258907.html)


### 2、znode节点属性
```
[value]
cZxid = 0x2
ctime = Wed Apr 18 19:46:14 CST 2018
mZxid = 0x2
mtime = Wed Apr 18 19:46:14 CST 2018
pZxid = 0x5
cversion = 1
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 4
numChildren = 1
```
<table style="text-align:left;font-size:14px">
<tr>
    <td  style="border-top:double;">属性</td>
    <td style="border-top:double;">含义</td>
</tr>
<tr>
    <td>cversion</td> <td>子节点的版本号</td>
</tr>
<tr>
    <td>dataVersion</td> <td>当前节点的值的版本号</td>
</tr>
<tr>
    <td>aclVersion</td> <td>当前节点的acl的版本号</td>
</tr>
<tr>
    <td style="border-top:double;">cZxid</td>
    <td style="border-top:double;">当前节点被创建的事务ID</td>
</tr>
<tr>
    <td>mZxid</td> <td>当前节点被修改的事务ID</td>
</tr>
<tr>
    <td>pZxid</td> <td>子节点最后一次被更改(子节点的增删改）的事务ID</td>
</tr>
<tr>
    <td>ctime</td> <td>当前节点的创建时间</td>
</tr>
<tr>
    <td>mtime</td> <td>当前节点最后一次修改的时间</td>
</tr>
<tr>
    <td style="border-top:double;">ephemeralOwner</td>
    <td style="border-top:double;">
        如果当前节点是临时节点，此属性记录创建该节点的会话ID<br>
        如果不是临时节点，此属性等于0
    </td>
</tr>
<tr>
    <td style="border-top:double;">dataLength</td>
    <td style="border-top:double;">当前节点的值的长度</td>
</tr>
<tr>
    <td style="border-bottom:double;">numChildren</td>
    <td style="border-bottom:double;">子节点数</td>
</tr>
</table>

> version属性，代表的是版本号，是一种乐观锁。<br>
> （数据库中的数据行一般会有一个字段来维护当前数据的版本）<br>

> 这里以 `dataVersion`为例，说一下版本号的作用：<br>
> 现有：服务端S、客户端A、客户端B<br>
> 步骤1：客户端A增加节点node1，版本号0<br>
> 步骤2：客户端B获取了节点node1<br>
> 步骤3：客户端A修改节点的值，此时版本号变成1<br>
> 那么：步骤3之后，客户端B也去修改node1，会怎样？<br>
> 因为node1的版本号是1，但是在客户端B上，版本号是0，即对B来讲，node1节点被别的客户端修改过，服务端S不建议B去修改，应该是去获取之后，在去修改。<br>

### 3、命令操作
#### 3.1、查看某节点下的子节点
> `ls path [watch]`<br>
> `ls2 path [watch]`<br>
> `ls2`扩展了`ls`，是`ls`和`stat`的合并
``` cmd
> ls /
[zookeeper]
```
> `/` 是根，不论查找、创建、修改、删除，都是以`/`开始的。<br>
> `zookeeper`节点是zookeeper自动创建的持久化节点。

#### 3.2、创建节点
> `create [-s] [-e] path data acl`<br>
> `-s` 表示节点是否有序<br>
> `-e` 表示是否为临时节点<br>
> 默认情况下，是持久化节点<br>
``` cmd
> create /xych xych
Created /xych
> ls /
[zookeeper, xych]
> ls /xych
[]
```
> 创建子节点时，必须带上父节点路径（同时也是用于删除、修改、获取、查询）
``` cmd
> create /xych/lanboo lanboo
Created /xych/lanboo
> ls /
[zookeeper, xych]
> ls /xych
[lanboo]
```
> 父节点不存在，不能创建子节点
``` cmd
> create /xych1/lanboo lanboo
Node does not exist: /xych1/lanboo
```

#### 3.3、获取节点信息
> `get path [watch]`    // 获取节点的值和属性<br>
> `stat path [watch]`   // 获取节点属性
``` cmd
> get /xych
xych                                    // stat命令，这行不打印
cZxid = 0x2
ctime = Wed Apr 18 19:46:14 CST 2018
mZxid = 0x2
mtime = Wed Apr 18 19:46:14 CST 2018
pZxid = 0x5
cversion = 1
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 4
numChildren = 1
```

#### 3.4、修改节点信息
> `set path data [version]`
``` cmd
> set /xych/lanboo Lanboo              //zookeeper大小写敏感
cZxid = 0x5
ctime = Wed Apr 18 23:45:33 CST 2018
mZxid = 0xc
mtime = Thu Apr 19 00:52:51 CST 2018
pZxid = 0x5
cversion = 0
dataVersion = 1                       // dataVersion 增加了
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 6
numChildren = 0
> get /xych/lanboo
Lanboo
cZxid = 0x5
ctime = Wed Apr 18 23:45:33 CST 2018
mZxid = 0xc
mtime = Thu Apr 19 00:52:51 CST 2018
pZxid = 0x5
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 6
numChildren = 0
> set /xych/lanboo lanboo 0
version No is not valid : /xych/lanboo              // 提示版本号不对，不允许修改
```

#### 3.5、删除节点
##### 3.5.1、`delete path [version]`
> 当将要被删除的节点存在子节点时，这个节点不允许删除
``` cmd
> ls /xych
[lanboo1, lanboo]
> delete /xych/lanboo1                        // 删除成功后，没有打印信息
> ls /xych
[lanboo]
> delete /xych
Node not empty: /xych                         // 提示节点不是空的，不允许删除
```
##### 3.5.2、`rmr path`
> 删除节点 <br>
> 与`delete`不同的是，可以删除带子节点的节点

#### 3.6、ACL相关
> `setAcl path acl`<br>
> `getAcl path`<br>

详见1.6


#### 3.7、节点配额（资源的配置）
##### 3.7.1、`setquota -n|-b val path`
> -n 设置某节点能够有多少个子节点 <br>
> -b 设置某节点的长度 <br>
``` cmd 
> setquota -n 5 /xych
> setquota -b 100 /xych
```
##### 3.7.2、`listquota path`
> 获取某节点的配额
##### 3.7.3、delquota [-n|-b] path
> 删除配额 <br>
``` cmd 
> delquota -n 5 /xych
> delquota -b 100 /xych
```

#### 3.8、强制同步
> `sync path` <br>
> 在集群环境中，当客户端跟新了某数据，请求成功的标识是：半数以上的`follower`节点生效时（`leader`向`follower`同步数据），就认为客户端的这次请求生效。<br>
> 那不可避免的是，存在一段时间，某些节点上的数据是旧的，那么为了保证所有的节点数据都是新值，就可以用`sync`命令。


#### 3.9、辅助命令
> `help` 查看命令<br>
> `history` 显示最近的操作<br>
> `redo cmdno` 重新执行，与`history`连用<br>



#### 3.10、连接服务端、断开服务端
> `connect ip:port`<br>
> `close`<br>

