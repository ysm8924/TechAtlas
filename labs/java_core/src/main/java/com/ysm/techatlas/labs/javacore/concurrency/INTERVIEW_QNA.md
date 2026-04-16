# 多线程与异步编程：大厂高频面试题大赏

在多线程面试中，面试官通常会从**理论对比**、**底层原理**和**手写代码**三个维度来考察。这里我为你梳理了最高频的 5 道理论题和 3 道手写代码题。

---

## 一、 理论与原理对比篇

### 1. `sleep()` 和 `wait()` 的区别是什么？
*   **所属类不同**：`sleep()` 是 `Thread` 类的静态方法；`wait()` 是 `Object` 类的实例方法。
*   **【核心区别】对锁的处理不同**：`sleep()` 会抱着锁睡觉（**不释放锁**）；`wait()` 会主动**释放锁**，让出 CPU，直到被 `notify()` 唤醒。
*   **使用场景限制**：`wait()` 必须在 `synchronized` 同步代码块内使用，否则抛异常；`sleep()` 可以在任何地方使用。

### 2. `synchronized` 和 `ReentrantLock` 有什么区别？
*   **层级不同**：`synchronized` 是 JVM 层面的关键字（底层依靠 `monitor` 对象）；`ReentrantLock` 是 API 层面的类（J.U.C 包下，依靠 AQS 实现）。
*   **灵活性**：`synchronized` 是隐式锁，自动释放，不够灵活。`ReentrantLock` 是显式锁，必须手动 `lock()` 并在 `finally` 块中 `unlock()`，但它提供了**公平锁**、**可中断获取锁**、**超时尝试获取锁**等高级特性。
*   **条件变量**：`ReentrantLock` 可以绑定多个 `Condition`（条件变量），实现精确唤醒指定的线程；`synchronized` 只能用 `wait/notify` 随机唤醒或全部唤醒。

### 3. ThreadLocal 是什么？为什么会导致内存泄漏？
*   **作用**：提供**线程局部变量**。多个线程访问同一个 `ThreadLocal` 时，每个线程实际上读写的是自己内部的一份副本，互不干扰（例如 Looper 的存储）。
*   **泄露原理**：`ThreadLocal` 底层依靠当前线程内部的 `ThreadLocalMap` 存储数据。Map 的 `Key` 是 `ThreadLocal` 对象的**弱引用 (WeakReference)**，而 `Value` 是强引用。当 `ThreadLocal` 被回收时，Key 变成了 null，但 Value 依然被强引用着。如果当前线程（例如线程池中的核心线程）迟迟不销毁，这个 Value 就会永远停留在内存中导致泄露。
*   **解法**：用完后务必调用 `threadLocal.remove()`。

### 4. 线程池 (ThreadPoolExecutor) 的核心参数与执行流程？
这是必考题！七大参数：`corePoolSize` (核心线程), `maximumPoolSize` (最大线程), `keepAliveTime` (存活时间), `unit`, `workQueue` (任务队列), `threadFactory`, `handler` (拒绝策略)。
*   **执行流程 (非常反直觉，必须死记)**：
    1.  任务来了，先看核心线程满了没。没满，**创建核心线程**执行。
    2.  满了，任务直接被**塞进队列 (Queue)** 里排队。
    3.  注意！如果核心线程满了，且**队列也塞满了**，此时才会去**创建非核心线程（直到达到最大线程数）**来执行任务。
    4.  如果最大线程数也达到了，触发**拒绝策略**。

---

## 二、 手写代码实战篇

面试官经常会要求白板写出以下三种代码（实战代码在 `ConcurrencyInterviewLab.java` 中已为你实现）：

1.  **手写死锁 (Deadlock)**：考察你对锁获取顺序的理解。
2.  **交替打印 ABC (精准通信)**：考察你对 `ReentrantLock` 和 `Condition` 或信号量 `Semaphore` 的运用。
3.  **线程池执行流验证**：要求用代码证明“先填满队列，再创建非核心线程”这一反直觉理论。