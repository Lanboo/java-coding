package com.xych.proxy.jdkproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkProxy implements InvocationHandler
{
    private Object target = null;
    private Log log = null;

    public <T> JdkProxy(T target, Log log)
    {
        super();
        this.target = target;
        this.log = log;
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance()
    {
        Class<?> clazz = this.target.getClass();
        /**
         * Proxy.newProxyInstance 用来产生某接口的代理类
         * 第一个参数：被代理类的类加载器
         * 第二个参数：被代理类的所有接口
         * 第三个参数：InvocationHandler，这里面定义了invoke方法。当代理类调用某方法时，实际上执行的是invoke方法
         */
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
    }

    /**
     * 这是代理后代理类在调用任意一个方法的入口
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        if(method.getName().startsWith("save"))
        {
            System.out.println("JdkProxy:走的是save方法");
            log.doSaveLog();
            return method.invoke(this.target, args);
        }
        else if(method.getName().startsWith("update"))
        {
            System.out.println("JdkProxy:走的是update方法");
            log.doUpdateLog();
            return method.invoke(this.target, args);
        }
        return method.invoke(this.target, args);
    }
}
