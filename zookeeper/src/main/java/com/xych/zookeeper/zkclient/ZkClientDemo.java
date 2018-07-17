package com.xych.zookeeper.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import com.xych.zookeeper.constant.BaseConstants;
import com.xych.zookeeper.zkclient.model.User;
import com.xych.zookeeper.zkclient.serializer.JacksonSerializer;

public class ZkClientDemo
{
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
     * 
     * @author 晓月残魂
     * @date 2018年6月24日下午10:19:37
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
     * 
     * @author 晓月残魂
     * @date 2018年6月24日下午10:19:23
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
     * 
     * @author 晓月残魂
     * @date 2018年6月24日下午10:19:12
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
     * 
     * @author 晓月残魂
     * @date 2018年6月24日下午10:19:00
     */
    private static void existsNode()
    {
        boolean b = zkClient.exists("/user");
        System.out.println("/user节点是否存在：" + b);
    }

    /**
     * 创建节点
     * 
     * @author 晓月残魂
     * @date 2018年6月24日下午10:18:47
     */
    private static void createNode()
    {
        User user = new User("xych", 24);
        String path = zkClient.create("/user", user, CreateMode.EPHEMERAL);
        System.out.println("创建节点：" + path);
    }

    /**
     * 创建连接
     * 
     * @author 晓月残魂
     * @date 2018年6月24日下午10:18:37
     */
    private static void connect()
    {
        //zkClient = new ZkClient(BaseConstants.CONNECT_STRING);
        //zkClient = new ZkClient(BaseConstants.CONNECT_STRING, 5000);
        //zkClient = new ZkClient(new ZkConnection(BaseConstants.CONNECT_STRING), 5000);
        //zkClient = new ZkClient(new ZkConnection(BaseConstants.CONNECT_STRING, 8000), 5000);
        /**
         * SerializableSerializer JDK序列化机制
         */
        //zkClient = new ZkClient(new ZkConnection(BaseConstants.CONNECT_STRING), 5000, new SerializableSerializer());
        /**
         * JacksonSerializer 自定义的序列化方式（这里采用Jackson）
         */
        zkClient = new ZkClient(new ZkConnection(BaseConstants.CONNECT_STRING), 5000, new JacksonSerializer<User>(User.class));
        System.out.println("建立连接");
    }
}
