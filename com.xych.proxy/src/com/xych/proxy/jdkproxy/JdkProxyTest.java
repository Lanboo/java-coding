package com.xych.proxy.jdkproxy;

import java.lang.reflect.Proxy;

public class JdkProxyTest
{
    public static void main(String[] args)
    {
        IDao daoA = new DaoAImpl();
        IDao daoB = new DaoBImpl();
        Log log = new Log();
        JdkProxy proxyA = new JdkProxy(daoA, log);
        JdkProxy proxyB = new JdkProxy(daoB, log);
        IDao proxyDaoA = proxyA.getInstance();
        IDao proxyDaoB = proxyB.getInstance();
        proxyDaoA.save();
        System.out.println("--------------===========--------------------");
        proxyDaoB.update();
        System.out.println("--------------===========--------------------");
        System.out.println(daoA);
        System.out.println(daoB);
        System.out.println(proxyDaoA);
        System.out.println(proxyDaoB);
    }

    public static void proxy()
    {
        IDao daoA = new DaoAImpl();
        Log log = new Log();
        IDao proxyDaoA = (IDao) Proxy.newProxyInstance(IDao.class.getClassLoader(), new Class[] { IDao.class }, new JdkProxy(daoA, log));
        proxyDaoA.save();
    }
}
