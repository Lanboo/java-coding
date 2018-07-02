[toc]

## Zookeeper开源客户端ZkClient的使用_Watcher事件

### 1、Zookeeper 知识回顾
#### 原生API：写操作触发[读操作绑定的事件]
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

>具体可以查看[zookeeper原生API的使用](zookeeper学习笔记_05_原生API的使用.md)的章节4


### 2、ZkClient的Listener
``` java
// 添加Listener
public void subscribeStateChanges(final IZkStateListener listener)
public void subscribeDataChanges(String path, IZkDataListener listener)
public List<String> subscribeChildChanges(String path, IZkChildListener listener)
// 移除Listener
public void unsubscribeStateChanges(IZkStateListener stateListener)
public void unsubscribeDataChanges(String path, IZkDataListener dataListener)
public void unsubscribeChildChanges(String path, IZkChildListener childListener)
public void unsubscribeAll()
```

### 3、Demo
#### 3.1、Listener
代码详见：com.xych.zookeeper.zkclient.listener

#### 3.2、ZKClientWatcherDemo
``` java
@Slf4j
public class ZKClientWatcherDemo
{
    // 集群环境用,隔开
    public static final String CONNECTSTRING = "192.168.27.133:2181";
    private static ZkClient zkClient;

    public static void main(String[] args) throws IOException
    {
        connect();// 创建连接
        addListener();// 添加listener
        createNode();// 创建节点 
        editNode();// 修改节点
        createChildNode();// 添加子节点
        editChildNode();// 修改子节点
        deleteChildNode();// 删除子节点
        deleteNode();// 删除节点
    }

    private static void deleteChildNode()
    {
        log.info("删除/xych/child");
        zkClient.delete("/xych/child", -1);
    }

    private static void editChildNode()
    {
        log.info("修改子节点/xych/child");
        User user = new User("xych2", 24);
        zkClient.writeDataReturnStat("/xych/child", user, -1);
    }

    private static void createChildNode()
    {
        log.info("创建子节点");
        User user = new User("child", 3);
        zkClient.create("/xych/child", user, CreateMode.EPHEMERAL);
    }

    private static void deleteNode()
    {
        log.info("删除/xych");
        zkClient.delete("/xych", -1);
    }

    private static void editNode()
    {
        log.info("修改节点/xych");
        User user = new User("xych2", 24);
        zkClient.writeDataReturnStat("/xych", user, -1);
    }

    private static void createNode()
    {
        log.info("创建节点：{}", "/xych");
        User user = new User("xych", 24);
        zkClient.create("/xych", user, CreateMode.PERSISTENT);//临时节点不能添加字节点
    }

    private static void addListener()
    {
        zkClient.subscribeStateChanges(new StateListener());
        DataListener dataListener = new DataListener();
        ChildListener childListener = new ChildListener();
        zkClient.subscribeDataChanges("/xych", dataListener);
        zkClient.subscribeChildChanges("/xych", childListener);
        zkClient.subscribeDataChanges("/xych/child", dataListener);
        zkClient.subscribeChildChanges("/xych/child", childListener);
    }

    private static void connect()
    {
        zkClient = new ZkClient(new ZkConnection(CONNECTSTRING), 5000, new JacksonSerializer<User>(User.class));
        log.info("建立连接");
    }
}
/* 运行结果
00:09:51.414 msg:[建立连接]
00:09:53.095 msg:[创建节点：/xych]
00:09:53.443 msg:[ChildListener：/xych的子节点发生变化，子节点数量=0]
00:09:53.443 msg:[ChildListener：/xych的子节点有[]]
00:09:53.468 msg:[DataListener：节点/xych被改变，value=[User [name=xych, age=24]]]
00:10:15.183 msg:[修改节点/xych]
00:10:15.202 msg:[DataListener：节点/xych被改变，value=[User [name=xych2, age=24]]]
00:10:21.040 msg:[创建子节点]
00:10:21.050 msg:[ChildListener：/xych/child的子节点发生变化，子节点数量=0]
00:10:21.051 msg:[ChildListener：/xych/child的子节点有[]]
00:10:21.052 msg:[DataListener：节点/xych/child被改变，value=[User [name=child, age=3]]]
00:10:21.053 msg:[ChildListener：/xych的子节点发生变化，子节点数量=1]
00:10:21.054 msg:[ChildListener：/xych的子节点有[child]]
00:10:40.047 msg:[修改子节点/xych/child]
00:10:40.054 msg:[DataListener：节点/xych/child被改变，value=[User [name=xych2, age=24]]]
00:10:49.052 msg:[删除/xych/child]
00:10:49.065 msg:[ChildListener：/xych/child的子节点发生变化，子节点数量=0]
00:10:49.066 msg:[DataListener：节点/xych/child被删除]
00:10:49.067 msg:[ChildListener：/xych的子节点发生变化，子节点数量=0]
00:10:49.067 msg:[ChildListener：/xych的子节点有[]]
00:10:53.280 msg:[删除/xych]
00:10:53.284 msg:[ChildListener：/xych的子节点发生变化，子节点数量=0]
00:10:53.285 msg:[DataListener：节点/xych被删除]
*/
```
### 4、结论
<table>
    <tr>
        <td></td>
        <td colspan="2">"/path"</td>
        <td colspan="2">"/path/child"</td>
    </tr>
    <tr>
        <td>写操作所触发的Listener</td>
        <td>IZkDataListener</td>
        <td>IZkChildListener</td>
        <td>IZkDataListener</td>
        <td>IZkChildListener</td>
    </tr>
    <tr>
        <td>create("/path")</td>
        <td>√</td>
        <td>√</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>delete("/path")</td>
        <td>√</td>
        <td>√</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>setData("/path")</td>
        <td>√</td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td>create("/path/child")</td>
        <td></td>
        <td>√</td>
        <td>√</td>
        <td>√</td>
    </tr>
    <tr>
        <td>delete("/path/child")</td>
        <td></td>
        <td>√</td>
        <td>√</td>
        <td>√</td>
    </tr>
    <tr>
        <td>setData("/path/child")</td>
        <td></td>
        <td></td>
        <td>√</td>
        <td></td>
    </tr>
</table>

