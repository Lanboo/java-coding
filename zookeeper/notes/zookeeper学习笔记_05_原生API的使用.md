[toc]

## zookeeper 原生API的使用
### 1、准备工作
#### 1.1、jar包
``` xml
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.8</version>
</dependency>
```
#### 1.2、准备工作
本人使用的是VMware + CentOS7环境，即虚拟机，这里需要将zookeeper客户端访问接口对外暴露。<br>
参考：[Centos防火墙设置与端口开放的方法](https://blog.csdn.net/u011846257/article/details/54707864)
``` linux
// 在指定区域打开端口
firewall-cmd --zone=public --add-port=80/tcp
firewall-cmd --zone=public --add-port=80/tcp --permanent    // 永久生效

// 重启防火墙
firewall-cmd --reload
```
### 2、创建连接、创建节点、修改节点、删除节点
#### 2.1、创建连接
``` java
// org.apache.zookeeper.ZooKeeper
public ZooKeeper(String connectString, int sessionTimeout, Watcher watcher)
public ZooKeeper(String connectString, int sessionTimeout, Watcher watcher, boolean canBeReadOnly)
public ZooKeeper(String connectString, int sessionTimeout, Watcher watcher, long sessionId, byte[] sessionPasswd)
public ZooKeeper(String connectString, int sessionTimeout, Watcher watcher, long sessionId, byte[] sessionPasswd, boolean canBeReadOnly)
```
参数|含义
:-|:-
connectString|Zookeeper服务器的地址和端口号`ip:port`。集群环境使用逗号隔开。
sessionTimeout|超时时间，超过一定时间不在连接
watcher|监听事件
canBeReadOnly|表示当前new的对象只能执行读操作，不能执行写操作。默认：false
sessionId|因Zookeeper的重连机制，即断开连接后，在一定时间内可再次连接，保持同一个会话。<br>`zookeeper.getSessionId()`可以在断开连接前获取sessionId
sessionPasswd|因Zookeeper的重连机制，即断开连接后，在一定时间内可再次连接，保持同一个会话。<br>`zookeeper.getSessionPasswd()`sessionPasswd


参考：[Zookeeper实例原生API--复用sessionId和sessionPasswd](https://blog.csdn.net/andy2019/article/details/73065449)
#### 2.2、创建节点
``` java
String create(final String path, byte[] data, List<ACL> acl, CreateMode createMode)
void create(final String path, byte[] data, List<ACL> acl, CreateMode createMode,  StringCallback cb, Object ctx)
```

参数|含义
:-|:-
path|被创建节点的路径
data|被创建节点的值
acl|acl策略。详见[zookeeper客户端的使用](zookeeper学习笔记_04_客户端的使用.md)的1.6章节
createMode|节点类型，临时或者持久，有序节点等。详见[zookeeper客户端的使用](zookeeper学习笔记_04_客户端的使用.md)的1.2、1.3章节
cb|异步创建方法参数。注册的回调函数，需实现StringCallback接口。数据节点创建完成之后，会调用此方法进行业务逻辑处理。主要针对`public void processResult(int rc, String path, Object ctx, String name)`接口进行重写。<br>参考：[Zookeeper客户端API之创建节点（七）](https://blog.csdn.net/wo541075754/article/details/65625481)
ctx|异步创建方法参数。用户传递一个对象，可以在回调方法执行时使用

- <b>临时节点下不能创建子节点</b><br>
- <b>因为是原生API，故不能在没有父节点的前提下直接创建子节点</b><br>

##### 2.2.1、关于StringCallback
转自：[Zookeeper客户端API之创建节点（七）](https://blog.csdn.net/wo541075754/article/details/65625481)

`StringCallback`接口继承了`AsyncCallback`接口，来实现回调时的业务处理。<br>
其中`AsyncCallback`接口还包8个回调接口：`StatCallback`、`DataCallback`、`ACLCallback`、`ChildrenCallback`、`Children2Callback`、`VoidCallback`、`MultiCallback`、`StringCallback`。可以在不同的异步接口中实现不同的回调接口。

`StringCallback`接口的`public void processResult(int rc, String path, Object ctx, String name)`方法。

参数|含义
:-|:-
rc|服务器的响应码，即`Event.KeeperState`，0表示调用成功，-4表示连接已断开，-110表示指定节点已存在，-112表示会话已过期。
path|调用create方法时传入的path。
ctx|调用create方法时传入的ctx。
name|创建成功的节点名称。


#### 2.3、修改节点
``` java
Stat setData(final String path, byte data[], int version)
public void setData(final String path, byte data[], int version, StatCallback cb, Object ctx)
```
参数|含义
:-|:-
path|被修改节点的路径
data|被修改节点的新值
version|代表节点的版本号，如果该值与zookeeper服务器此节点的dataVersion属性值不相同，则修改失败。 `-1`代表忽略版本号的作用，强制修改！
cb|异步创建方法参数。注册的回调函数，需实现StatCallback接口。数据节点创建完成之后，会调用此方法进行业务逻辑处理。
ctx|异步创建方法参数。用户传递一个对象，可以在回调方法执行时使用
返回值Stat|描述一个节点的信息，具体可以查看[zookeeper客户端的使用](zookeeper学习笔记_04_客户端的使用.md)的章节2

- 关于`StatCallback`，与`StringCallback`类似。
    - 实现的方法是`public void processResult(int rc, String path, Object ctx, Stat stat)`

#### 2.4、删除节点
``` java
void delete(final String path, int version)
void delete(final String path, int version, VoidCallback cb, Object ctx)
```
- 与修改节点`setData`方法类似。
- <b>因为是原始API，不允许删除存在子节点的节点</b>

#### 2.5、Demo

``` java
public class ApiZookeeper
{
    // 集群环境用,隔开
    private static final String CONNECTSTRING = "192.168.27.128:2181";
    private static ZooKeeper zookeeper;

    public static void main(String[] args) throws Exception
    {
        connect();
        createNode("/xych", "xych");
        // createNode("/xych1/lanboo","lanboo"); // 报错
        setNode("/xych", "XYCH", -1);
        deleteNode("/xych", -1);
    }

    /**
     * 删除节点
     */
    public static void deleteNode(String nodePath, int version) throws Exception
    {
        zookeeper.delete(nodePath, version);
        System.out.println("删除成功");
    }

    /**
     * 修改节点
     */
    public static void setNode(String nodePath, String value, int version) throws Exception
    {
        Stat stat = zookeeper.setData(nodePath, value.getBytes(), version);
        System.out.println("修改成功 " + stat);
    }

    /**
     * 创建节点
     */
    public static void createNode(String nodePath, String value) throws Exception
    {
        String result = zookeeper.create(nodePath, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("创建成功 " + result);
    }

    /**
     * 创建连接
     */
    public static void connect() throws Exception
    {
        // 使用CountDownLatch，使主线程等待
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zookeeper = new ZooKeeper(CONNECTSTRING, 10000, new Watcher()
        {
            @Override
            public void process(WatchedEvent event)
            {
                if(event.getState() == Event.KeeperState.SyncConnected)
                {
                    System.out.println("Watcher " + zookeeper.getState());
                    countDownLatch.countDown();
                }
            }
        });
        System.out.println("connect " + zookeeper.getState());
        countDownLatch.await();
        System.out.println("connect " + zookeeper.getState());
        // zookeeper.close();
    }
}
/* 输出结果
connect CONNECTING
Watcher CONNECTED
connect CONNECTED
创建成功 /xych
修改成功 46,47,1524152856857,1524152856864,1,0,0,72057600313458694,4,0,46

删除成功
*/
```

### 3、exists、getData、getChildren
#### 3.1、exists
> 判断某节点是否存在
``` java
Stat exists(String path, boolean watch)
Stat exists(final String path, Watcher watcher)
void exists(String path, boolean watch, StatCallback cb, Object ctx)
void exists(final String path, Watcher watcher, StatCallback cb, Object ctx)
```
参数|含义
:-|:-
path|被判断节点的路径
watch|是否添加一个默认Watcher
watcher|监听事件
cb|异步创建方法参数。注册的回调函数，需实现StatCallback接口。数据节点创建完成之后，会调用此方法进行业务逻辑处理。
ctx|异步创建方法参数。用户传递一个对象，可以在回调方法执行时使用

关于`StatCallback`可以参考本文2.2.1。

#### 3.2、getData
> 获取节点内容，同步获取和异步获取
``` java
byte[] getData(String path, boolean watch, Stat stat)
byte[] getData(final String path, Watcher watcher, Stat stat)
void getData(String path, boolean watch, DataCallback cb, Object ctx)
void getData(final String path, Watcher watcher, DataCallback cb, Object ctx)
```
参数|含义
:-|:-
path|节点的路径
watch|是否注册一个默认Watcher
watcher|监听事件
cb|异步创建方法参数。注册的回调函数，需实现DataCallback接口。数据节点创建完成之后，会调用此方法进行业务逻辑处理。
ctx|异步创建方法参数。用户传递一个对象，可以在回调方法执行时使用

关于`DataCallback`可以参考本文2.2.1。
#### 3.3、getChildren
> 获取子节点列表，同步获取和异步获取
``` java
List<String> getChildren(String path, boolean watch)
List<String> getChildren(final String path, Watcher watcher)
List<String> getChildren(String path, boolean watch, Stat stat)
List<String> getChildren(final String path, Watcher watcher, Stat stat)
void getChildren(String path, boolean watch, ChildrenCallback cb, Object ctx)
void getChildren(String path, boolean watch, Children2Callback cb, Object ctx)
void getChildren(final String path, Watcher watcher, ChildrenCallback cb, Object ctx)
void getChildren(final String path, Watcher watcher, Children2Callback cb, Object ctx)
```
参数|含义
:-|:-
path|节点的路径
watch|是否注册一个默认Watcher
watcher|监听事件
stat|描述一个节点的信息<br>会将path指定的节点的信息更新<br>具体可以查看[zookeeper客户端的使用](zookeeper学习笔记_04_客户端的使用.md)的章节2
cb|异步创建方法参数。注册的回调函数，需实现ChildrenCallback、Children2Callback接口。数据节点创建完成之后，会调用此方法进行业务逻辑处理。
ctx|异步创建方法参数。用户传递一个对象，可以在回调方法执行时使用

关于`ChildrenCallback`、`Children2Callback`可以参考本文2.2.1。

### 4、Watcher事件监控
- <b>Watcher是一次性的，用完就会失效</b><br>

参考：[ZooKeeper监听机制](https://www.cnblogs.com/programlearning/archive/2017/05/10/6834963.html)
<table style="text-align:left;font-size:14px">
<tr>
    <td  style="border-top:double;">事件类型</td>
    <td style="border-top:double;">含义</td>
</tr>
<tr>
    <td>EventType.None</td> <td>与服务器建立连接时触发</td>
</tr>
<tr>
    <td>EventType.NodeCreated</td> <td>被监控的节点被创建触发</td>
</tr>
<tr>
    <td>EventType.NodeDeleted</td> <td>被监控的节点被删除触发</td>
</tr>
<tr>
    <td>EventType.NodeDataChanged</td> <td>被监控的节点被修改触发</td>
</tr>
<tr>
    <td style="border-bottom:double;">EventType.NodeChildrenChanged</td>
    <td style="border-bottom:double;">被监控的节点的子节点<b>数量</b>发生改变时触发</td>
</tr>
</table>

<table style="text-align:left;font-size:14px">
<tr>
    <td  style="border-top:double;">操作</td>
    <td style="border-top:double;">Event For "/path"</td>
    <td style="border-top:double;">Event For "/path/child"</td>
</tr>
<tr>
    <td>create("/path")</td> <td>EventType.NodeCreated</td> <td>NA</td>
</tr>
<tr>
    <td>delete("/path")</td> <td>EventType.NodeDeleted</td> <td>NA</td>
</tr>
<tr>
    <td>setData("/path")</td> <td>EventType.NodeDataChanged</td> <td>NA</td>
</tr>
<tr>
    <td>create("/path/child")</td> <td>EventType.NodeChildrenChanged</td> <td>EventType.NodeCreated</td>
</tr>
<tr>
    <td>delete("/path/child")</td> <td>EventType.NodeChildrenChanged</td> <td>EventType.NodeDeleted</td>
</tr>
<tr>
    <td style="border-bottom:double;">setData("/path/child")</td>
    <td style="border-bottom:double;">NA</td>
    <td style="border-bottom:double;">EventType.NodeDataChanged</td>
</tr>
</table>



> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
> <br>
