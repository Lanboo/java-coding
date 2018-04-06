package com.xych.proxy.jdkproxy;

public class DaoAImpl implements IDao
{
    @Override
    public void save()
    {
        System.out.println("IDao的A实现类：保存了一个Pojo");
    }

    @Override
    public void update()
    {
        System.out.println("IDao的A实现类：修改了一个Pojo");
    }
}
