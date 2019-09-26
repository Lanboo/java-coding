package com.xych.jvm.practice;

public class ClassLoderTest2 {
    public static void main(String[] args) throws Exception {
        test1();
        // test2();
    }

    // `classLoader#loadClass` 实例方法，根据传入的类全限定名称，加载Class对象到内存。
    public static void test1() throws Exception {
        Class<?> clazz = ClassLoderTest2.class.getClassLoader().loadClass("com.xych.jvm.practice.ATest");
        System.out.println(clazz);
        Object obj = clazz.newInstance();
        System.out.println(obj);
    }

    // `Class.forName` 静态方法，根据传入的类全限定名称，加载Class对象到内存，并执行类初始化操作。
    public static void test2() throws Exception {
        Class<?> clazz = Class.forName("com.xych.jvm.practice.ATest");
        System.out.println(clazz);
        Object obj = clazz.newInstance();
        System.out.println(obj);
    }
}

class ATest {
    static {
        System.out.println("class static code");
    }
}
