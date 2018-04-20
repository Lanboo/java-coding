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

转自：[ZooKeeper监听机制](https://www.cnblogs.com/programlearning/archive/2017/05/10/6834963.html)

#### 4.1、事件类型
事件类型|含义
:-|:-
EventType.None|与服务器建立连接时触发
EventType.NodeCreated|被监控的节点被创建触发
EventType.NodeDeleted|被监控的节点被删除触发
EventType.NodeDataChanged|被监控的节点被修改触发
EventType.NodeChildrenChanged|被监控的节点的子节点<b>数量</b>发生改变时触发

#### 4.2、读操作绑定事件
读操作|含义
:-|:-
new ZooKeeper|不会指定某节点，故触发类型为`EventType.None`
exists|判断某节点是否存在，同时对该节点添加Watcher
getData|获取某节点的值，同时对该节点添加Watcher
getChildren|获取某节点的子节点列表，同时对该节点添加Watcher

#### 4.3、写操作触发事件
写操作|Event For "/path"|Event For "/path/child"
:-|:-|:-
create("/path")|EventType.NodeCreated|-
delete("/path")|EventType.NodeDeleted|-
setData("/path")|EventType.NodeDataChanged|-
create("/path/child")|EventType.NodeChildrenChanged|EventType.NodeCreated
delete("/path/child")|EventType.NodeChildrenChanged|EventType.NodeDeleted
setData("/path/child")|-|EventType.NodeDataChanged
####

#### 4.4、写操作触发[读操作绑定的事件]
<table>
    <tr>
        <td></td>
        <td colspan="3">"/path"</td>
        <td colspan="3">"/path/child"</td>
    </tr>
    <tr>
        <td>写操作所触发的绑定事件</td>
        <td>exists</td>
        <td>getData</td>
        <td>getChildren</td>
        <td>exists</td>
        <td>getData</td>
        <td>getChildren</td>
    </tr>
    <tr>
        <td>create("/path")</td>
        <td>√</td>
        <td>√</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>delete("/path")</td>
        <td>√</td>
        <td>√</td>
        <td>√</td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>setData("/path")</td>
        <td>√</td>
        <td>√</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>create("/path/child")</td>
        <td></td>
        <td></td>
        <td>√</td>
        <td>√</td>
        <td>√</td>
        <td></td>
    </tr>
    <tr>
        <td>delete("/path/child")</td>
        <td></td>
        <td></td>
        <td>√</td>
        <td>√</td>
        <td>√</td>
        <td>√</td>
    </tr>
    <tr>
        <td>setData("/path/child")</td>
        <td></td>
        <td></td>
        <td></td>
        <td>√</td>
        <td>√</td>
        <td></td>
    </tr>
</table>

> - `create("/path/child")`和`delete("/path/child")`<b>只会触发</b>`getChildren`对`"/path"`的绑定事件；<br>
> - 不会触发`exists`、`getData`对`"/path"`的绑定事件。<br>

#### 4.4、Demo

``` java
public class ApiZookeeperWatcher implements Watcher
{
    // 集群环境用,隔开
    private static final String CONNECTSTRING = "192.168.27.131:2181";
    private static ZooKeeper zookeeper;
    // 使用CountDownLatch，使主线程等待
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception
    {
        connect();
        createWatcher("/xych", "xych");
        setDataWatcher("/xych", "XYCH");
        deleteWatcher("/xych");
        // 这里"/xych"已被删除，再次创建。（此时"/xych"没有任何Watcher）
        // 注意：临时节点下不能创建节点
        if(zookeeper.exists("/xych1", false) == null)
        {
            zookeeper.create("/xych1", "xych".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        createWatcher_Children("/xych1", "/lanboo", "lanboo");
        setDataWatcher_Children("/xych1", "/lanboo", "LANBOO");
        deleteWatcher_Children("/xych1", "/lanboo");
    }

    public static void deleteWatcher_Children(String path, String children) throws Exception
    {
        countDownLatch = new CountDownLatch(1);
        // 利用getChildren，对父节点添加Watcher
        List<String> pathChildren = zookeeper.getChildren(path, new ApiZookeeperWatcher());
        System.out.println("deleteWatcher_Children：" + path + "的子节点：" + pathChildren);
        // 利用exists，对子节点添加Watcher
        Stat childrenStat = zookeeper.exists(path + children, new ApiZookeeperWatcher());
        if(pathChildren != null && childrenStat != null)
        {
            zookeeper.delete(path + children, -1);
            countDownLatch.await();
        }
        System.out.println();
    }

    public static void setDataWatcher_Children(String path, String children, String value) throws Exception
    {
        countDownLatch = new CountDownLatch(1);
        // 利用getChildren，对父节点添加Watcher
        List<String> pathChildren = zookeeper.getChildren(path, new ApiZookeeperWatcher());
        System.out.println("setDataWatcher_Children：" + path + "的子节点：" + pathChildren);
        // 利用exists，对子节点添加Watcher
        Stat childrenStat = zookeeper.exists(path + children, new ApiZookeeperWatcher());
        if(pathChildren != null && childrenStat != null)
        {
            zookeeper.setData(path + children, value.getBytes(), -1);
            countDownLatch.await();
        }
        System.out.println();
    }

    /**
     * 对某节点添加Warcher，对该节点添加子节点
     */
    public static void createWatcher_Children(String path, String children, String value) throws Exception
    {
        countDownLatch = new CountDownLatch(2);
        // 利用getChildren，对父节点添加Watcher
        List<String> pathChildren = zookeeper.getChildren(path, new ApiZookeeperWatcher());
        System.out.println("createWatcher_Children：" + path + "的子节点：" + pathChildren);
        // 利用exists，对子节点添加Watcher
        Stat childrenStat = zookeeper.exists(path + children, new ApiZookeeperWatcher());
        if(pathChildren != null && childrenStat == null)
        {
            zookeeper.create(path + children, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            countDownLatch.await();
        }
        System.out.println();
    }

    /**
     * 对setData监控
     * 做法：
     * 1、利用exists，判断某节点是否存在，同时对该节点添加一个Watcher
     * 2、删除该节点
     */
    public static void deleteWatcher(String path) throws Exception
    {
        countDownLatch = new CountDownLatch(1);
        // 判断"/xych"节点是否存在，并且对此节点添加一个Watcher
        Stat stat = zookeeper.exists(path, new ApiZookeeperWatcher());
        System.out.println("deleteWatcher：" + path + "的节点属性：" + stat);
        if(stat != null)
        {
            zookeeper.delete(path, -1);
            countDownLatch.await();
        }
        System.out.println();
    }

    /**
     * 对setData监控
     * 做法：
     * 1、利用getData，获取某节点的value，同时对该节点添加一个Watcher
     * 2、创建该节点
     */
    public static void setDataWatcher(String path, String value) throws Exception
    {
        countDownLatch = new CountDownLatch(1);
        Stat stat = new Stat();
        byte[] bytes = zookeeper.getData(path, new ApiZookeeperWatcher(), stat);
        System.out.println("setDataWatcher：" + path + "的原始值 = " + new String(bytes));
        System.out.println("setDataWatcher：" + path + "的节点信息 = " + stat);
        zookeeper.setData(path, value.getBytes(), -1);
        countDownLatch.await();
        System.out.println();
    }

    /**
     * 对create监控
     * 做法：
     * 1、利用exists，判断某节点是否存在，同时对该节点添加一个Watcher
     * 2、创建该节点
     */
    public static void createWatcher(String path, String value) throws Exception
    {
        countDownLatch = new CountDownLatch(1);
        // 判断path节点是否存在，并且对此节点添加一个Watcher
        Stat stat = zookeeper.exists(path, new ApiZookeeperWatcher());
        System.out.println("createWatcher：" + path + "的节点属性：" + stat);
        if(stat == null)
        {
            zookeeper.create(path, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            countDownLatch.await();
        }
        System.out.println();
    }

    /**
     * 创建连接
     */
    public static void connect() throws Exception
    {
        zookeeper = new ZooKeeper(CONNECTSTRING, 10000, new ApiZookeeperWatcher());
        System.out.println("connect " + zookeeper.getState());
        countDownLatch.await();
        System.out.println("connect " + zookeeper.getState());
        System.out.println();
    }

    @Override
    public void process(WatchedEvent watchedEvent)
    {
        // 只在连接成功的情况下，进行事件监听
        if(watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected)
        {
            try
            {
                if(Event.EventType.None == watchedEvent.getType())
                {
                    System.out.println("Watcher：" + watchedEvent.getState() + "-->" + watchedEvent.getType());
                }
                else if(Event.EventType.NodeCreated == watchedEvent.getType())
                {
                    System.out.println("Watcher：" + watchedEvent.getPath() + "被创建");
                }
                else if(Event.EventType.NodeDeleted == watchedEvent.getType())
                {
                    System.out.println("Watcher：" + watchedEvent.getPath() + "被删除");
                }
                else if(Event.EventType.NodeDataChanged == watchedEvent.getType())
                {
                    System.out.println("Watcher：" + watchedEvent.getPath() + "被修改");
                }
                else if(Event.EventType.NodeChildrenChanged == watchedEvent.getType())
                {
                    System.out.println("Watcher：" + watchedEvent.getPath() + "的子节点数量发生改变");
                }
                countDownLatch.countDown();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("Watcher：" + this);
    }
}
```
输出
``` java
connect CONNECTING
Watcher：SyncConnected-->None
Watcher：com.xych.zookeeper.api.ApiZookeeperWatcher@7b23ec81
connect CONNECTED

createWatcher：/xych的节点属性：null
Watcher：/xych被创建
Watcher：com.xych.zookeeper.api.ApiZookeeperWatcher@1a7d63bd

setDataWatcher：/xych的原始值 = xych
setDataWatcher：/xych的节点信息 = 165,165,1524221468877,1524221468877,0,0,0,72057600313458724,4,0,165

Watcher：/xych被修改
Watcher：com.xych.zookeeper.api.ApiZookeeperWatcher@7aa0aa6f

deleteWatcher：/xych的节点属性：165,166,1524221468877,1524221468884,1,0,0,72057600313458724,4,0,165

Watcher：/xych被删除
Watcher：com.xych.zookeeper.api.ApiZookeeperWatcher@bb15e0d

createWatcher_Children：/xych1的子节点：[]
Watcher：/xych1/lanboo被创建
Watcher：com.xych.zookeeper.api.ApiZookeeperWatcher@2cce4a9a
Watcher：/xych1的子节点数量发生改变
Watcher：com.xych.zookeeper.api.ApiZookeeperWatcher@2d8e2810

setDataWatcher_Children：/xych1的子节点：[lanboo]
Watcher：/xych1/lanboo被修改
Watcher：com.xych.zookeeper.api.ApiZookeeperWatcher@7333dbfe

deleteWatcher_Children：/xych1的子节点：[lanboo]
Watcher：/xych1/lanboo被删除
Watcher：com.xych.zookeeper.api.ApiZookeeperWatcher@7165a6c4
Watcher：/xych1的子节点数量发生改变
Watcher：com.xych.zookeeper.api.ApiZookeeperWatcher@2f21b320
Watcher：/xych1的子节点数量发生改变
Watcher：com.xych.zookeeper.api.ApiZookeeperWatcher@31ecabd5
```