package com.xych.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Curator基本操作
 * @Description
 * @author 晓月残魂
 * @CreateDate 2018年7月8日下午10:58:50
 */
public class CuratorBaseDemo
{
    // 集群环境用,隔开
    private static final String CONNECTSTRING = "192.168.27.131:2181";
    private static CuratorFramework curatorFramework;

    public static void main(String[] args)
    {
        connect();
    }

    private static void connect()
    {
        curatorFramework = CuratorFrameworkFactory.builder() //
            .connectString(CONNECTSTRING) //
            .sessionTimeoutMs(3000) //
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))//
            .build();
        curatorFramework.start();
    }
}
