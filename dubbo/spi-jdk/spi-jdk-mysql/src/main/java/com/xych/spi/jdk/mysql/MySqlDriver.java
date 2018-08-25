package com.xych.spi.jdk.mysql;

import com.xych.spi.jdk.api.IXychDriver;

public class MySqlDriver implements IXychDriver
{
    @Override
    public void connection(String path)
    {
        System.out.println("MySQL impl:path=" + path);
    }
}
