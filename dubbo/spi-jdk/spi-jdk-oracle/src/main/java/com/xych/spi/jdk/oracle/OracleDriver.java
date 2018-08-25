package com.xych.spi.jdk.oracle;

import com.xych.spi.jdk.api.IXychDriver;

public class OracleDriver implements IXychDriver
{
    @Override
    public void connection(String path)
    {
        System.out.println("Oracle impl:path=" + path);
    }
}
