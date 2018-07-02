package com.xych.zookeeper.zkclient;

import java.io.IOException;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import com.xych.zookeeper.zkclient.listener.ChildListener;
import com.xych.zookeeper.zkclient.listener.DataListener;
import com.xych.zookeeper.zkclient.listener.StateListener;
import com.xych.zookeeper.zkclient.model.User;
import com.xych.zookeeper.zkclient.serializer.JacksonSerializer;

import lombok.extern.slf4j.Slf4j;

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
        deleteChildNode();
        deleteNode();// 删除节点
    }

    private static void deleteChildNode()
    {
        boolean b = zkClient.delete("/xych/child", -1);
        log.info("是否删除/xych/child：{}", b);
    }

    private static void editChildNode()
    {
        User user = new User("xych2", 24);
        Stat stat = zkClient.writeDataReturnStat("/xych/child", user, -1);
        log.info("修改子节点/xych/child：{}", stat.toString().trim());
    }

    private static void createChildNode()
    {
        User user = new User("child", 3);
        String path = zkClient.create("/xych/child", user, CreateMode.EPHEMERAL);
        log.info("创建子节点：{}", path);
    }

    private static void deleteNode()
    {
        boolean b = zkClient.delete("/xych", -1);
        log.info("是否删除/xych：{}", b);
    }

    private static void editNode()
    {
        User user = new User("xych2", 24);
        Stat stat = zkClient.writeDataReturnStat("/xych", user, -1);
        log.info("修改节点/xych：{}", stat.toString().trim());
    }

    private static void createNode()
    {
        User user = new User("xych", 24);
        String path = zkClient.create("/xych", user, CreateMode.PERSISTENT);//临时节点不能添加字节点
        log.info("创建节点：{}", path);
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
