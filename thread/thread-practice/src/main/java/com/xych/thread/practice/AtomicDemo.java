package com.xych.thread.practice;

import java.util.concurrent.TimeUnit;

public class AtomicDemo {
    private static long cnt = 0;
    private static volatile long count = 0;

    private static void incr() {
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
        cnt++;
        count++;
    }

    public static void main(String[] args) throws InterruptedException {
        for(int i = 0; i < 1000; i++) {
            new Thread(() -> AtomicDemo.incr()).start();
        }
        TimeUnit.SECONDS.sleep(6);
        System.out.println(AtomicDemo.cnt);
        System.out.println(AtomicDemo.count);
    }
}
