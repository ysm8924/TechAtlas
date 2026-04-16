package com.ysm.techatlas.labs.javacore.concurrency;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 层次二：工程化调度进阶 (BlockingQueue, ReadWriteLock, Semaphore, CompletableFuture)
 */
public class ConcurrencyAdvancedLab {

    private static final String TAG = "ConcurrencyAdvanced";

    // =================================================================================
    // 实战 1: 生产者-消费者模型 (BlockingQueue)
    // 解决：线程间解耦、自动背压处理
    // =================================================================================
    public static void testProducerConsumer() throws InterruptedException {
        System.out.println("\n--- 1. Testing Producer-Consumer (BlockingQueue) ---");
        // 容量为 3 的阻塞队列
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(3);

        Runnable producer = () -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    String data = "Data-" + i;
                    queue.put(data); // 如果队列满了，生产者会自动挂起等待
                    System.out.println("[" + TAG + "] Produced: " + data);
                    Thread.sleep(100);
                }
            } catch (InterruptedException ignored) {}
        };

        Runnable consumer = () -> {
            try {
                while (true) {
                    String data = queue.take(); // 如果队列空了，消费者会自动挂起等待
                    System.out.println("[" + TAG + "] Consumed: " + data);
                    Thread.sleep(300); // 模拟消费较慢
                }
            } catch (InterruptedException ignored) {}
        };

        new Thread(producer).start();
        Thread cThread = new Thread(consumer);
        cThread.setDaemon(true); // 设为守护线程，随主线程结束
        cThread.start();

        Thread.sleep(2000); // 观察一段时间
    }

    // =================================================================================
    // 实战 2: 读写锁 (ReadWriteLock)
    // 解决：读多写少场景下的并发性能
    // =================================================================================
    private static final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static String sharedData = "Initial";

    public static void testReadWriteLock() {
        System.out.println("\n--- 2. Testing ReadWriteLock ---");
        
        Runnable readTask = () -> {
            rwLock.readLock().lock();
            try {
                System.out.println("[" + TAG + "] Reading: " + sharedData + " on " + Thread.currentThread().getName());
                Thread.sleep(100);
            } catch (InterruptedException ignored) {} finally {
                rwLock.readLock().unlock();
            }
        };

        Runnable writeTask = () -> {
            rwLock.writeLock().lock();
            try {
                System.out.println("[" + TAG + "] --- WRITING START ---");
                sharedData = "New Data " + System.currentTimeMillis();
                Thread.sleep(200);
                System.out.println("[" + TAG + "] --- WRITING END ---");
            } catch (InterruptedException ignored) {} finally {
                rwLock.writeLock().unlock();
            }
        };

        // 启动 3 个读线程，1 个写线程
        for (int i = 0; i < 3; i++) new Thread(readTask).start();
        new Thread(writeTask).start();
        new Thread(readTask).start();
    }

    // =================================================================================
    // 实战 3: 信号量 (Semaphore)
    // 解决：资源并发限制（如限流）
    // =================================================================================
    public static void testSemaphore() {
        System.out.println("\n--- 3. Testing Semaphore (Permit: 2) ---");
        Semaphore semaphore = new Semaphore(2); // 模拟只有 2 个许可

        Runnable task = () -> {
            try {
                semaphore.acquire(); // 申请许可
                System.out.println("[" + TAG + "] " + Thread.currentThread().getName() + " acquired a permit.");
                Thread.sleep(500);
                System.out.println("[" + TAG + "] " + Thread.currentThread().getName() + " releasing.");
                semaphore.release(); // 释放许可
            } catch (InterruptedException ignored) {}
        };

        for (int i = 0; i < 4; i++) new Thread(task).start();
    }

    // =================================================================================
    // 实战 4: 现代异步编排 (CompletableFuture)
    // 解决：异步任务链式调用、合并结果
    // =================================================================================
    public static void testCompletableFuture() throws ExecutionException, InterruptedException {
        System.out.println("\n--- 4. Testing CompletableFuture (Task Chaining) ---");
        
        CompletableFuture<String> taskA = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            return "User(101)";
        });

        CompletableFuture<String> taskB = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            return "Order(999)";
        });

        // 合并两个并行任务的结果
        CompletableFuture<String> combined = taskA.thenCombine(taskB, (user, order) -> {
            return "Combined: " + user + " with " + order;
        });

        System.out.println("[" + TAG + "] Result: " + combined.get());
    }

    public static void runTests() {
        System.out.println("========== Concurrency Advanced Tests Start ==========");
        try {
            testProducerConsumer();
            testReadWriteLock();
            testSemaphore();
            testCompletableFuture();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
