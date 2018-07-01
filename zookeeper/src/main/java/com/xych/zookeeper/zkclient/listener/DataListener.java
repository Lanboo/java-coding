package com.xych.zookeeper.zkclient.listener;

import org.I0Itec.zkclient.IZkDataListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataListener implements IZkDataListener
{
    @Override
    public void handleDataChange(String dataPath, Object data) throws Exception
    {
        log.info("DataListener：节点{}被改变，value=[{}]", dataPath, data.toString());
    }

    @Override
    public void handleDataDeleted(String dataPath) throws Exception
    {
        log.info("DataListener：节点{}被删除", dataPath);
    }
}
