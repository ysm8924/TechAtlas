# Java 并发编程进阶面试题 (J.U.C 专题)

## 1. ConcurrentHashMap 的底层原理？(Java 8+)
*   **数据结构**：数组 + 链表 + 红黑树。
*   **锁粒度**：Java 7 使用分段锁 (Segment)；Java 8 舍弃分段锁，直接使用 **Node 数组 + CAS + synchronized**。
*   **为什么快？**：它只锁住链表的头节点（桶位），而不像 `Hashtable` 锁住整个 Map。

## 2. CAS 的 ABA 问题是什么？如何解决？
*   **现象**：线程 A 读到值是 1，准备改。线程 B 进来把 1 改成 2，又改回 1。线程 A 检查发现还是 1，以为没变过，修改成功。
*   **风险**：在某些内存复用的链表结构中，这会导致逻辑错误。
*   **解法**：使用 **版本号 (Version)** 或 **时间戳**。Java 提供了 `AtomicStampedReference`，它不仅对比值，还对比版本号。

## 3. 什么是 AQS (AbstractQueuedSynchronizer)？
*   **地位**：它是整个 Java 并发包的灵魂。`ReentrantLock`, `CountDownLatch`, `Semaphore` 都是基于它实现的。
*   **原理**：内部维护一个 **`volatile int state` (同步状态)** 和一个 **双向同步队列 (FIFO)**。
    *   当 `state=0`，线程通过 CAS 拿到锁并把 `state` 置为 1。
    *   如果抢锁失败，线程会被封装成 Node 节点塞进队列并挂起。

## 4. 读写锁 (ReadWriteLock) 的插队策略？
*   **写锁优先级**：为了防止“写饥饿”（读线程太多，写线程永远拿不到锁），Java 的读写锁在有写线程排队时，会阻止新的读线程进入。

## 5. ForkJoinPool 是什么？与普通线程池的区别？
*   **核心特性**：**工作窃取 (Work-Stealing) 算法**。
*   **区别**：普通线程池的线程如果干完活了就会闲着；ForkJoinPool 的线程干完活后，会去偷其他忙碌线程队列末尾的任务来帮着做，从而把 CPU 压榨到极致。
