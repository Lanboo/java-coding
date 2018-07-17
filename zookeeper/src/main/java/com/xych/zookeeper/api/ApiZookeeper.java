package com.xych.zookeeper.api;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.xych.zookeeper.constant.BaseConstants;

public class ApiZookeeper
{
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
        zookeeper = new ZooKeeper(BaseConstants.CONNECT_STRING, 10000, new Watcher()
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
