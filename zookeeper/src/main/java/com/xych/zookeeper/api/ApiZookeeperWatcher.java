package com.xych.zookeeper.api;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.Watcher.Event;

public class ApiZookeeperWatcher implements Watcher
{
    // 集群环境用,隔开
    private static final String CONNECTSTRING = "192.168.27.128:2181";
    private static ZooKeeper zookeeper;
    // 使用CountDownLatch，使主线程等待
    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args)
    {
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
                    countDownLatch.countDown();
                    System.out.println("Watcher" + watchedEvent.getState() + "-->" + watchedEvent.getType());
                }
                else if(Event.EventType.NodeCreated == watchedEvent.getType())
                {
                    System.out.println("节点创建" + watchedEvent.getPath());
                }
                else if(Event.EventType.NodeDeleted == watchedEvent.getType())
                {
                    System.out.println("节点删除" + watchedEvent.getPath());
                }
                else if(Event.EventType.NodeDataChanged == watchedEvent.getType())
                {
                    System.out.println("节点修改" + watchedEvent.getPath());
                }
                else if(Event.EventType.NodeChildrenChanged == watchedEvent.getType())
                {
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
