package com.xych.thread.practice;

public class Synchronized {
    private Object obj = new Object();

    // 修饰实例方法
    // 对象锁
    // 使用this作为锁，相当于method2()
    public synchronized void method() {
        // do some thing
    }

    public void method2() {
        // 修饰代码块
        // 对象锁
        // 相比method()，代码块控制的粒度更细
        synchronized(this) {
            // do some thing
        }
    }

    public void method3() {
        // 修饰代码块
        // 对象锁
        synchronized(obj) {
        }
    }

    // 修饰静态方法
    // 类锁
    public synchronized static void staticMethod() {
    }

    public void method4() {
        // 修饰代码块
        // 类锁
        synchronized(Synchronized.class) {
        }
    }
}
