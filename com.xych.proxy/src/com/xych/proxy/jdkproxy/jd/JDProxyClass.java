package com.xych.proxy.jdkproxy.jd;

import java.io.FileOutputStream;

import com.xych.proxy.jdkproxy.IDao;

import sun.misc.ProxyGenerator;

public class JDProxyClass
{
    public static void main(String[] args) throws Exception
    {
        byte[] data = ProxyGenerator.generateProxyClass("$Proxy0", new Class[] { IDao.class });
        FileOutputStream os = new FileOutputStream("D:/$Proxy0.class");
        os.write(data);
        os.flush();
        os.close();
    }
}
