package com.ysm.techatlas.labs.javacore.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 层次一：底层基石（并发的三大灾难与解法）
 */
public class ConcurrencyBasicsLab {

    private static final String TAG = "ConcurrencyBasics";

    // =================================================================================
    // 灾难 1: 可见性 (Visibility) 问题
    // =================================================================================
    // 【现象】：如果不加 volatile，子线程可能会一直卡在死循环里，因为它在自己的 CPU 缓存中
    // 看到的 isRunning 永远是 true，根本不知道主线程已经把它改成 false 了。
    // 【解法】：加上 volatile。它强迫所有线程读写这个变量时，必须直接读写主内存。
    private static volatile boolean isRunning = true;

    public static void testVisibility() throws InterruptedException {
        System.out.println("\n--- Testing Visibility (volatile) ---");
        
        Thread bgThread = new Thread(() -> {
            System.out.println("[" + TAG + "] Background thread started.");
            long count = 0;
            while (isRunning) {
                // 死循环，模拟耗时监控
                count++;
            }
            System.out.println("[" + TAG + "] Background thread stopped. Count: " + count);
        });

        bgThread.start();
        
        Thread.sleep(100); // 让子线程飞一会
        System.out.println("[" + TAG + "] Main thread modifying isRunning to false...");
        isRunning = false; // 如果没有 volatile，bgThread 可能永远停不下来！
        
        bgThread.join(); // 等待子线程结束
    }

    // =================================================================================
    // 灾难 2: 原子性 (Atomicity) 问题
    // =================================================================================
    private static int unsafeCount = 0;
    
    // 【解法 1：悲观锁】每次只能进一个人
    private static int syncCount = 0;
    
    // 【解法 2：乐观锁/CAS】底层使用 Unsafe 类的 compareAndSwap 指令（极其高效）
    private static final AtomicInteger atomicCount = new AtomicInteger(0);

    public static void testAtomicity() throws InterruptedException {
        System.out.println("\n--- Testing Atomicity (synchronized vs CAS) ---");
        
        // CountDownLatch: 等待 10 个线程全部执行完毕
        CountDownLatch latch = new CountDownLatch(10);
        
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                unsafeCount++; // 危险操作！它分为三步：读取、加一、写回。容易被别的线程打断覆盖。
                
                synchronized (ConcurrencyBasicsLab.class) {
                    syncCount++; // 安全，但因为要排队获取锁，性能开销大
                }
                
                atomicCount.incrementAndGet(); // 安全，基于 CPU 硬件级别的 CAS 指令，性能极高
            }
            latch.countDown();
        };

        // 启动 10 个线程，每个线程加 1000 次。期望结果是 10000。
        for (int i = 0; i < 10; i++) {
            new Thread(task).start();
        }

        latch.await(); // 阻塞主线程，直到 latch 归零
        
        System.out.println("[" + TAG + "] Unsafe Count (Should be 10000, usually less): " + unsafeCount);
        System.out.println("[" + TAG + "] Sync Count: " + syncCount);
        System.out.println("[" + TAG + "] Atomic Count: " + atomicCount.get());
    }

    public static void runTests() {
        System.out.println("========== Concurrency Basics Tests Start ==========");
        try {
            testVisibility();
            testAtomicity();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("========== Concurrency Basics Tests End ==========\n");
    }
}
