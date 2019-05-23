package com.bofa.client.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.client.util
 * @date 2019/4/26
 */
public class PrintStreamDelegate {

    private static Scanner sc = new Scanner(System.in);

    private static ReentrantLock lock = new ReentrantLock();

    private static ThreadPoolExecutor executor =
            new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, new LinkedBlockingDeque<>()
                    , new ThreadFactoryBuilder().setNameFormat("printStream-worker")
                    .build());

    static {
//        Thread delegateWorker = new Thread(() -> {
//            while (Thread.interrupted()) {
//                if (executor.getQueue().isEmpty() && !taskList.isEmpty()) {
//                    delegate0(taskList.pollFirst());
//                    try {
//                        barrier.await();
//                    } catch (InterruptedException | BrokenBarrierException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }, "delegateWorker");
//        delegateWorker.start();
    }

    public static String nextLine() {
        try {
            lock.lock();
            return sc.nextLine();
        } finally {
            lock.unlock();
        }
    }

    public static String next() {
        try {
            lock.lock();
            return sc.next();
        } finally {
            lock.unlock();
        }
    }


    public static void delegate(Runnable runnable) {
        assert runnable != null;
        try {
            lock.lock();
            Future<?> future = executor.submit(runnable);
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } finally {
            lock.unlock();
        }
    }

    public static void delegateUnfair(Runnable runnable) {
        assert runnable != null;
        Future<?> future = executor.submit(runnable);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
//        try {
//            TimeUnit.SECONDS.sleep(5);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        delegate(() -> {
//            System.out.println("hello");
//            for (int i = 0; i < 10; i++) {
//                try {
//                    TimeUnit.SECONDS.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("enter loop rest " + i + " times");
//            }
//        });
//        delegate(() -> {
//            System.out.println("world");
//        });
    }
//
//    /**
//     * fair
//     *
//     * @param runnable
//     */
//    public static void delegateFair(Runnable runnable) {
//        delegateSubmit(runnable);
//        taskList.addLast(runnable);
//        signalDelegateWorker();
//    }
//
//    private static void delegateSubmit(Runnable runnable) {
//        boolean action = false;
//        try {
//            if (lock.tryLock()) {
//                executor.execute(runnable);
//                action = true;
//            }
//        } finally {
//            if (action) {
//                lock.unlock();
//            }
//        }
//    }
//
//    private static void signalDelegateWorker() {
//        if (barrier.getNumberWaiting() == 1) {
//            try {
//                barrier.await();
//            } catch (InterruptedException | BrokenBarrierException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
