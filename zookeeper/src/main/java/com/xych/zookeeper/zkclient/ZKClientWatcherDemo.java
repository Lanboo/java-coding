package com.xych.zookeeper.zkclient;

import java.io.IOException;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.zookeeper.CreateMode;

import com.xych.zookeeper.constant.BaseConstants;
import com.xych.zookeeper.zkclient.listener.ChildListener;
import com.xych.zookeeper.zkclient.listener.DataListener;
import com.xych.zookeeper.zkclient.listener.StateListener;
import com.xych.zookeeper.zkclient.model.User;
import com.xych.zookeeper.zkclient.serializer.JacksonSerializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZKClientWatcherDemo
{
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
        zkClient = new ZkClient(new ZkConnection(BaseConstants.CONNECT_STRING), 5000, new JacksonSerializer<User>(User.class));
        log.info("建立连接");
    }
}
