package com.xych.jvm.practice;

public class ClassLoderTest {
    public static void main(String[] args) {
        System.out.println(new ClassLoderTest().getClass().getClassLoader().getParent().getParent());
        System.out.println(new ClassLoderTest().getClass().getClassLoader().getParent());
        System.out.println(new ClassLoderTest().getClass().getClassLoader());
        System.out.println("=====================================");
        System.out.println(new Object().getClass().getClassLoader());
        System.out.println(new String().getClass().getClassLoader());
    }
}
