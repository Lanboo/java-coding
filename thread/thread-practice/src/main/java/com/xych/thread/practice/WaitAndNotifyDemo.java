package com.xych.thread.practice;

public class WaitAndNotifyDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread myThread = new Thread() {
            @Override
            public void run() {
                synchronized(this) {
                    try {
                        System.out.println("before notify");
                        notify();
                        Thread.sleep(1000);
                        System.out.println("after notify");
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
                System.out.println("before wait");
                myThread.wait();
                System.out.println("after wait");
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
