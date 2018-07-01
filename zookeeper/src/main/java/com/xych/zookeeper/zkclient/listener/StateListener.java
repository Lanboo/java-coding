package com.xych.zookeeper.zkclient.listener;

import org.I0Itec.zkclient.IZkStateListener;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StateListener implements IZkStateListener
{
    @Override
    public void handleStateChanged(KeeperState state) throws Exception
    {
        log.info("StateListener：连接状态变成了{}", state.name());
    }

    @Override
    public void handleNewSession() throws Exception
    {
        log.info("StateListener：产生了新的连接");
    }

    @Override
    public void handleSessionEstablishmentError(Throwable error) throws Exception
    {
        log.info("StateListener：发生了异常", error);
    }
}
