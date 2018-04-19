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
#### 2、创建连接、创建节点、修改节点、删除节点
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
> - `new ZooKeeper(String connectString, int sessionTimeout, Watcher watcher)`<br>
> `connectString`：连接字符串，指zookeeper服务器的`ip:port`，集群环境使用逗号`,`隔开。<br>
> `sessionTimeout`：超时时间，超过一定时间不在连接<br>
> `watcher`：监听事件。在这里当连接状态改变时触发。[注意Watcher是一次性的，用完就会失效]<br>

> - `String ZooKeeper.create(String path, byte[] data, List<ACL> acl, CreateMode createMode)`<br>
> `path`、`data`分别是节点路径和值<br>
> `acl` 是节点访问权限，谁？有什么权限？ 详见[zookeeper客户端的使用](zookeeper学习笔记_04_客户端的使用.md)的1.6章节<br>
> `createMode`节点类型，临时或者持久，有序节点等<br>
> 返回节点的路径
>   - <b>临时节点下不能创建子节点</b><br>
>   - <b>因为是原始API，故不能在没有父节点的前提下直接创建子节点</b><br>

> - `Stat ZooKeeper.setData(final String path, byte data[], int version)`<br>
> `path`、`data`分别是节点路径和新值<br>
> `version`代表节点的版本号，如果该值与zookeeper服务器此节点的dataVersion属性值不相同，则修改失败。`-1`代表忽略版本号的作用，强制修改！<br>
> `Stat`是描述一个节点的信息，具体可以查看[zookeeper客户端的使用](zookeeper学习笔记_04_客户端的使用.md)的章节2<br>

> - `void ZooKeeper.delete(final String path, int version)`<br>
> `path`分别是节点路径<br>
> `version`代表节点的版本号。具体作用与set相同。<br>
>   - <b>因为是原始API，不允许删除存在子节点的节点</b><br>

#### 3、exists、getData、getChildren



#### 4、Watcher事件监控
<b>Watcher是一次性的，用完就会失效</b><br>
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
