package com.xych.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import com.xych.zookeeper.constant.BaseConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * Curator基本操作
 * @Description
 * @author 晓月残魂
 * @CreateDate 2018年7月8日下午10:58:50
 */
@Slf4j
public class CuratorBaseDemo
{
    private static CuratorFramework curatorFramework;

    public static void main(String[] args) throws Exception
    {
        connect();
        createChildNode(); // 创建子节点：/XYCH/Lanboo/xych
        eidtNode(); // 编辑子节点/XYCH/Lanboo的值
        deleteNode(); // 删除根节点，递归删除子节点
    }

    private static void deleteNode() throws Exception
    {
        curatorFramework.usingNamespace("")
            .delete()
            .deletingChildrenIfNeeded()
            .forPath("/XYCH");
        log.info("删除成功");
    }

    private static void eidtNode() throws Exception
    {
        Stat stat2 = curatorFramework.setData() //
            .withVersion(-1) //
            .forPath("/Lanboo");
        log.info(stat2.toString().trim());
    }

    private static void createChildNode() throws Exception
    {
        String path = curatorFramework.create()
            .creatingParentsIfNeeded() // 父级节点不存在则递归创建
            .withMode(CreateMode.PERSISTENT) // 持久节点
            .forPath("/Lanboo/xych", "xych".getBytes());
        log.info("创建的节点为：{}", path);
    }

    private static void connect()
    {
        curatorFramework = CuratorFrameworkFactory.builder() //
            .connectString(BaseConstants.CONNECT_STRING) //
            .sessionTimeoutMs(3000) //
            .namespace("XYCH") //
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))//
            .build();
        curatorFramework.start();
        log.info("连接成功");
    }
}
