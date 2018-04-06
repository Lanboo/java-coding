package com.xych.proxy.staticproxy;

public class StaticProxyTest
{
    public static void main(String[] args)
    {
        StaticProxy staticProxy = new StaticProxy(new Dao(), new Log());
        staticProxy.save();
    }
}
