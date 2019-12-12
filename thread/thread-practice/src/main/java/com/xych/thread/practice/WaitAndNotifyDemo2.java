package com.xych.thread.practice;

public class WaitAndNotifyDemo2 {
    public static void main(String[] args) throws InterruptedException {
        Thread myThread = new Thread() {
            @Override
            public void run() {
                synchronized(this) {
                    try {
                        System.out.println("before thread notify");
                        notify();
                        System.out.println("after thread notify");
                        Thread.sleep(1000);
                        System.out.println("before thread wait");
                        this.wait();
                        System.out.println("after thread wait");
                        Thread.sleep(1000);
                    }
                    catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        synchronized(myThread) {
            try {
                myThread.start();
                Thread.sleep(1000);
                System.out.println("before main wait");
                myThread.wait();
                System.out.println("after main wait");
                Thread.sleep(1000);
                System.out.println("before main notify");
                myThread.notify();
                System.out.println("after main notify");
                Thread.sleep(1000);
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
