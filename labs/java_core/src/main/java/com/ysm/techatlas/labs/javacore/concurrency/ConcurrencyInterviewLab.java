package com.ysm.techatlas.labs.javacore.concurrency;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多线程大厂高频手写面试题实战
 */
public class ConcurrencyInterviewLab {

    private static final String TAG = "InterviewLab";

    // =================================================================================
    // 面试手写 1：手写一个死锁 (Deadlock)
    // 考点：嵌套获取两把锁，且获取顺序相反。
    // =================================================================================
    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public static void createDeadlock() {
        System.out.println("\n--- 1. Testing Deadlock (Daemon threads) ---");
        // 使用守护线程，防止死锁导致整个 JVM 无法退出
        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("[" + TAG + "] T1 got lockA, waiting for lockB...");
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                synchronized (lockB) {
                    System.out.println("[" + TAG + "] T1 got both locks!");
                }
            }
        });
        t1.setDaemon(true);

        Thread t2 = new Thread(() -> {
            synchronized (lockB) {
                System.out.println("[" + TAG + "] T2 got lockB, waiting for lockA...");
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                synchronized (lockA) {
                    System.out.println("[" + TAG + "] T2 got both locks!");
                }
            }
        });
        t2.setDaemon(true);

        t1.start();
        t2.start();
        // 主线程稍微等一下，让打印完成
        try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        System.out.println("[" + TAG + "] Main thread moves on, T1 and T2 are deadlocked forever!");
    }

    // =================================================================================
    // 面试手写 2：三个线程交替打印 A、B、C 各 3 次
    // 考点：ReentrantLock + 多个 Condition 的精准唤醒机制
    // =================================================================================
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition condA = lock.newCondition();
    private static final Condition condB = lock.newCondition();
    private static final Condition condC = lock.newCondition();
    private static int printState = 0; // 0=A, 1=B, 2=C

    public static void testAlternatePrint() throws InterruptedException {
        System.out.println("\n--- 2. Testing Alternate Printing (A-B-C) ---");
        CountDownLatch latch = new CountDownLatch(3);

        Thread threadA = new Thread(() -> printChar(0, condA, condB, "A", latch));
        Thread threadB = new Thread(() -> printChar(1, condB, condC, "B", latch));
        Thread threadC = new Thread(() -> printChar(2, condC, condA, "C", latch));

        threadA.start(); threadB.start(); threadC.start();
        latch.await();
    }

    private static void printChar(int targetState, Condition currentCond, Condition nextCond, String text, CountDownLatch latch) {
        for (int i = 0; i < 3; i++) {
            lock.lock();
            try {
                // 不属于自己的回合，就挂起等待
                while (printState != targetState) {
                    currentCond.await();
                }
                System.out.print(text + " ");
                // 状态流转，并精准唤醒下一个 Condition
                printState = (printState + 1) % 3;
                nextCond.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
        latch.countDown();
    }

    // =================================================================================
    // 面试验证 3：证明线程池的反直觉执行顺序（核心满了 -> 进队列 -> 队列满了 -> 扩容非核心）
    // =================================================================================
    public static void testThreadPoolBehavior() throws InterruptedException {
        System.out.println("\n\n--- 3. Testing ThreadPool Execution Sequence ---");
        
        // 核心参数：核心 2，最大 4，队列容量 2
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                2, 4, 
                10, TimeUnit.SECONDS, 
                new ArrayBlockingQueue<>(2),
                new ThreadPoolExecutor.AbortPolicy()
        );

        // 提交 6 个耗时任务，观察它们被谁执行，以及谁被拒绝
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            try {
                pool.execute(() -> {
                    System.out.println("[" + TAG + "] Task " + taskId + " is running on " + Thread.currentThread().getName());
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                });
                System.out.println("Successfully submitted Task " + taskId);
            } catch (RejectedExecutionException e) {
                System.out.println("Task " + taskId + " was REJECTED!");
            }
        }

        pool.shutdown();
        pool.awaitTermination(2, TimeUnit.SECONDS);
    }

    public static void runTests() {
        System.out.println("========== Concurrency Interview Tests Start ==========");
        createDeadlock();
        try {
            testAlternatePrint();
            testThreadPoolBehavior();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("========== Concurrency Interview Tests End ==========\n");
    }
}
