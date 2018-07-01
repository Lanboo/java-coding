package com.xych.zookeeper.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

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

    public static void main(String[] args)
    {
        connect();
        addListener();
    }

    private static void addListener()
    {
        zkClient.subscribeStateChanges(new StateListener());
        zkClient.subscribeDataChanges("/xych", new DataListener());
        zkClient.subscribeChildChanges("/xych", new ChildListener());
    }

    private static void connect()
    {
        zkClient = new ZkClient(new ZkConnection(CONNECTSTRING), 5000, new JacksonSerializer<User>(User.class));
        log.info("建立连接");
    }
}
