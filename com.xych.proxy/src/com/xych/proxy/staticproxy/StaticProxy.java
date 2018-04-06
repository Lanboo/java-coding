package com.xych.proxy.staticproxy;

public class StaticProxy
{
    private Dao dao = null;
    private Log log = null;

    public StaticProxy(Dao dao, Log log)
    {
        super();
        this.dao = dao;
        this.log = log;
    }

    public void save()
    {
        System.out.println("StaticProxy:开始代理");
        this.log.doLog();
        this.dao.save();
        System.out.println("StaticProxy:代理结束了");
    }
}
