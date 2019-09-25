package com.xych.thread.practice;

import java.util.concurrent.TimeUnit;

public class ThreadInterrupt {
    private static long i;

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                i++;
            }
            System.out.println(i);
        });
        thread.join();
        thread.start();
        TimeUnit.SECONDS.sleep(1);
        // 打断线程
        // 1、当thread处于wait/sleep/join时，thread会立即抛出InterruptedException，在try-catch下即可使thread执行完而结束线程
        // 2、也可以使用它作为一个标志位，用于退出while循环，从而使thread执行完而结束线程
        thread.interrupt();
        System.out.println(thread.isInterrupted());
    }
}
