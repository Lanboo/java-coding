[toc]

## Zookeeper开源客户端ZkClient的使用

### 1、创建连接、创建节点、修改节点、删除节点
#### 1.1、创建连接
``` java
// org.I0Itec.zkclient.ZkClient
public ZkClient(String zkServers)
public ZkClient(String zkServers, int connectionTimeout)
public ZkClient(String zkServers, int sessionTimeout, int connectionTimeout)
public ZkClient(String zkServers, int sessionTimeout, int connectionTimeout, ZkSerializer zkSerializer)
public ZkClient(IZkConnection connection)
public ZkClient(IZkConnection connection, int connectionTimeout)
public ZkClient(IZkConnection connection, int connectionTimeout, ZkSerializer zkSerializer)
```
``` java
// org.I0Itec.zkclient.IZkConnection 的实现类
public ZkConnection(String zkServers)
public ZkConnection(String zkServers, int sessionTimeOut)
```
<div class="xych-table" style="font-size:14px;">

参数|含义
:-|:-
zkServers|Zookeeper服务器的地址和端口号`ip:port`。集群环境使用逗号隔开。
sessionTimeout|会话超时时间。这里可以查看[zoo.cfg简介](zookeeper学习笔记_03_zoo.cfg配置简介.md)的会话时间设置。
connectionTimeout|连接时间
connection|一般指`IZkConnection`的实现类`ZkConnection`
zkSerializer|序列化，默认使用JDK的序列化方式。可以自定义，比如使用Jackson。
</div>

#### 1.2、创建节点
``` java
// org.I0Itec.zkclient.ZkClient
public String create(final String path, Object data, final CreateMode mode)
public String create(final String path, Object data, final List<ACL> acl, final CreateMode mode)
...
// 其他n多变种
```
<div class="xych-table" style="font-size:14px;">

参数|含义
:-|:-
path|被创建节点的路径
data|被创建节点的值。注意，会把`ZkSerializer`序列化之后的结果当成Zookeeper节点的值。
acl|acl策略。详见[zookeeper客户端的使用](zookeeper学习笔记_04_客户端的使用.md)的1.6章节
mode|节点类型：临时或者持久，有序节点等。详见[zookeeper客户端的使用](zookeeper学习笔记_04_客户端的使用.md)的1.2、1.3章节
</div>

#### 1.3、获取节点
``` java
// org.I0Itec.zkclient.ZkClient
public <T extends Object> T readData(String path) // 相当于returnNullIfPathNotExists = false
public <T extends Object> T readData(String path, boolean returnNullIfPathNotExists)
public <T extends Object> T readData(String path, Stat stat)
```
> 会被`ZkSerializer`反序列化
<div class="xych-table" style="font-size:14px;">

参数|含义
:-|:-
path|节点的路径
returnNullIfPathNotExists|当节点不存在时，是否返回null（否则throwj节点不存在的异常）
stat|描述一个节点的信息<br>会将path指定的节点的信息更新<br>具体可以查看[zookeeper客户端的使用](zookeeper学习笔记_04_客户端的使用.md)的章节2
</div>

#### 1.4、修改节点
``` java
// org.I0Itec.zkclient.ZkClient
public void writeData(String path, Object object)
public void writeData(String path, Object object, int version)
public Stat writeDataReturnStat(String path, Object object, int version)
```
<div class="xych-table" style="font-size:14px;">

参数|含义
:-|:-
path|节点的路径
object|被创建节点的值。注意，会把`ZkSerializer`序列化之后的结果当成Zookeeper节点的值。
version|代表节点的版本号，如果该值与zookeeper服务器此节点的dataVersion属性值不相同，则修改失败。<br>`-1`代表忽略版本号的作用，强制修改！
</div>

#### 1.5、删除节点
``` java
// org.I0Itec.zkclient.ZkClient
public boolean delete(final String path)
public boolean delete(final String path, final int version)
public boolean deleteRecursive(String path) // 删除当前节点及其子节点，当某个子节点删除失败时，停止执行，返回false
```
<div class="xych-table" style="font-size:14px;">

参数|含义
:-|:-
path|节点的路径
version|代表节点的版本号，如果该值与zookeeper服务器此节点的dataVersion属性值不相同，则修改失败。<br>`-1`代表忽略版本号的作用，强制修改！
</div>


#### 2.6、Demo
``` java
public class ZkClientDemo
{
    // 集群环境用,隔开
    public static final String CONNECTSTRING = "192.168.27.133:2181";
    private static ZkClient zkClient;

    public static void main(String[] args)
    {
        connect();// 创建连接
        createNode();// 创建节点 
        existsNode();// 节点是否存在
        getNode();// 获取节点信息
        setNode();// 修改节点
        deleteNode();// 删除节点
    }

    /**
     * 删除节点
     */
    private static void deleteNode()
    {
        //boolean b1 = zkClient.delete("/user");
        boolean b2 = zkClient.delete("/user", -1);
        //boolean b3 = zkClient.deleteRecursive("/user");// 删除含有子节点的节点
        System.out.println("是否删除：" + b2);
    }

    /**
     * 修改节点值
     */
    private static void setNode()
    {
        User user = new User("xych2", 24);
        //zkClient.writeData("/user", user);
        //zkClient.writeData("/user", user, -1);
        Stat stat = zkClient.writeDataReturnStat("/user", user, -1);
        System.out.println("重置/user：" + stat);
    }

    /**
     * 获取节点信息
     */
    private static void getNode()
    {
        User user = zkClient.readData("/user");
        System.out.println("节点值：" + user);
        Stat stat = new Stat();
        User user2 = zkClient.readData("/user", stat);
        System.out.println("节点值：" + user2);
        System.out.println("节点信息：" + stat);
    }

    /**
     * 节点是否存
     */
    private static void existsNode()
    {
        boolean b = zkClient.exists("/user");
        System.out.println("/user节点是否存在：" + b);
    }

    /**
     * 创建节点
     */
    private static void createNode()
    {
        User user = new User("xych", 24);
        String path = zkClient.create("/user", user, CreateMode.EPHEMERAL);
        System.out.println("创建节点：" + path);
    }

    /**
     * 创建连接
     */
    private static void connect()
    {
        //zkClient = new ZkClient(CONNECTSTRING);
        //zkClient = new ZkClient(CONNECTSTRING, 5000);
        //zkClient = new ZkClient(new ZkConnection(CONNECTSTRING), 5000);
        //zkClient = new ZkClient(new ZkConnection(CONNECTSTRING, 8000), 5000);
        /**
         * SerializableSerializer JDK序列化机制
         */
        //zkClient = new ZkClient(new ZkConnection(CONNECTSTRING), 5000, new SerializableSerializer());
        /**
         * JacksonSerializer 自定义的序列化方式（这里采用Jackson）
         */
        zkClient = new ZkClient(new ZkConnection(CONNECTSTRING), 5000, new JacksonSerializer<User>(User.class));
        System.out.println("建立连接");
    }
}
/* 输出结果
建立连接
创建节点：/user
/user节点是否存在：true
节点值：User [name=xych, age=24]
节点值：User [name=xych, age=24]
节点信息：18,18,1529846336413,1529846336413,0,0,0,72057751331602438,24,0,18

重置/user：18,19,1529846336413,1529846336446,1,0,0,72057751331602438,25,0,18

是否删除：true
*/
```