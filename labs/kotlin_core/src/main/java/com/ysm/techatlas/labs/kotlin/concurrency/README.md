# Kotlin 现代化并发与异步编程：协程的降维打击

在 Java 时代，我们依赖于线程池、锁（synchronized/ReentrantLock）和复杂的异步回调。Kotlin 通过**协程 (Coroutines)** 引入了“非阻塞式挂起”的概念，彻底改变了并发编程的范式。

## 1. 核心哲学：非阻塞 (Non-blocking) 与 挂起 (Suspend)
*   **Java 线程池**：当一个线程执行 IO 操作时，它会**阻塞**。这个昂贵的系统资源被死死占住，直到数据返回。
*   **Kotlin 协程**：当执行 `delay` 或挂起函数时，协程会被**挂起**。它会释放底层的线程去干别的活。等时间到了或数据回来了，协程再被**恢复**到某个线程上继续执行。这种“轻量级线程”的特性让我们可以同时开启 10 万个协程而不 OOM。

---

## 2. Kotlin 对 Java 并发场景的重构

| 场景 | Java 方案 | Kotlin 现代化方案 | 优势 |
| :--- | :--- | :--- | :--- |
| **原子性/锁** | `synchronized` / `Atomic` | **`Mutex` (互斥锁)** | `Mutex.lock()` 是**挂起**而非阻塞，不卡死线程 |
| **异步编排** | `CompletableFuture` | **`async` / `await`** | 像写同步代码一样写异步，消灭回调 |
| **并发限制** | `Semaphore` | **`Semaphore` (协程版)** | 也是挂起机制，更轻量 |
| **生产者-消费者** | `BlockingQueue` | **`Channel` (管道)** | 专门为协程设计的通信机制 |
| **数据流/响应式** | `RxJava` | **`Flow` (冷/热流)** | 协程原生支持，零开销切换线程 |

---

## 3. 结构化并发 (Structured Concurrency) —— 协程的杀手锏
这是 Java 线程池无法做到的。
*   **概念**：在一个父协程作用域内开启的所有子协程，都受到父协程的管理。
*   **优势**：如果父协程被取消，所有的子协程会自动被递归取消。这彻底解决了 Android 开发中因 Activity 销毁导致的**内存泄露**和**后台任务无法停止**的痛点。

---

## 4. 常见考点：Dispatcher 的选择
*   **`Dispatchers.Main`**：UI 线程。
*   **`Dispatchers.IO`**：网络、磁盘 IO。
*   **`Dispatchers.Default`**：CPU 密集型计算（如大型数组排序、JSON 解析）。
*   **`Dispatchers.Unconfined`**：不局限于任何线程（慎用，通常仅用于测试）。
