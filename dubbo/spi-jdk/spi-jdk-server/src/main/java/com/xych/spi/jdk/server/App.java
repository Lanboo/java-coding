package com.xych.spi.jdk.server;

import java.util.ServiceLoader;

import com.xych.spi.jdk.api.IXychDriver;

public class App
{
    public static void main(String[] args)
    {
        ServiceLoader<IXychDriver> serviceLoader = ServiceLoader.load(IXychDriver.class);
        for(IXychDriver xychDriver : serviceLoader)
        {
            xychDriver.connection("xych");
        }
    }
}
