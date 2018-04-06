package com.xych.proxy.cglibproxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CglibProxy implements MethodInterceptor
{
    private Object target = null;
    private Log log = null;

    public <T> CglibProxy(T target, Log log)
    {
        super();
        this.target = target;
        this.log = log;
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance()
    {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.target.getClass());
        enhancer.setCallback(this);
        return (T) enhancer.create();
        //如果出现异常：Exception in thread "main" java.lang.NoClassDefFoundError: org/objectweb/asm/Type
        //原因：因为很多字节码操作都用到asm.jar，故引入即可
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable
    {
        if(method.getName().startsWith("save"))
        {
            System.out.println("CglibProxy:走的是save方法");
            log.doSaveLog();
            return method.invoke(this.target, args);
        }
        else if(method.getName().startsWith("update"))
        {
            System.out.println("CglibProxy:走的是update方法");
            log.doUpdateLog();
            return method.invoke(this.target, args);
        }
        return method.invoke(this.target, args);
    }
}
