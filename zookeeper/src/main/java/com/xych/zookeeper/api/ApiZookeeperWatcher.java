package com.xych.zookeeper.api;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.ZooDefs;

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
