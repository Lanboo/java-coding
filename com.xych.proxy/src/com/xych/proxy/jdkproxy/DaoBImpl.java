package com.xych.proxy.jdkproxy;

public class DaoBImpl implements IDao
{
    @Override
    public void save()
    {
        System.out.println("IDao的B实现类：保存了一个Pojo");
    }

    @Override
    public void update()
    {
        System.out.println("IDao的B实现类：修改了一个Pojo");
    }
}
