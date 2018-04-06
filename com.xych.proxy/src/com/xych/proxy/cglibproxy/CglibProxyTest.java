package com.xych.proxy.cglibproxy;

public class CglibProxyTest
{
    public static void main(String[] args)
    {
        DaoAImpl daoA = new DaoAImpl();
        DaoBImpl daoB = new DaoBImpl();
        Log log = new Log();
        CglibProxy proxyA = new CglibProxy(daoA, log);
        CglibProxy proxyB = new CglibProxy(daoB, log);
        DaoAImpl proxyDaoA = proxyA.getInstance();
        DaoBImpl proxyDaoB = proxyB.getInstance();
        proxyDaoA.save();
        System.out.println("--------------===========--------------------");
        proxyDaoB.update();
    }
}
