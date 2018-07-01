package com.xych.zookeeper.zkclient.listener;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChildListener implements IZkChildListener
{
    @Override
    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception
    {
        if(currentChilds != null)
        {
            Integer len = currentChilds.size();
            log.info("ChildListener：{}的子节点发生变化，子节点数量={}", parentPath, len);
            log.info("ChildListener：{}的子节点有{}", parentPath, currentChilds.toString());
        }
        else
        {
            log.info("ChildListener：{}的子节点发生变化，子节点数量={}", parentPath, 0);
        }
    }
}
