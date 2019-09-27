package com.xych.jvm.practice;

public class StringConstantPool {
    public static void main(String[] args) {
        String a = "abc";
        String b = "abc";
        String c = new String(a);
        String d = c.intern();
        System.out.println(a == b);
        System.out.println(a == c);
        System.out.println(a == d);
        System.out.println(b == c);
        System.out.println(b == d);
        System.out.println(c == d);
        //
        String aa = new String(new char[] { 'a', 'a' });
        String bb = aa.intern();
        System.out.println(aa == bb);
    }
}
