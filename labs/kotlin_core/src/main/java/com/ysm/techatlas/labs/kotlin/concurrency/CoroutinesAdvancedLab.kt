package com.ysm.techatlas.labs.kotlin.concurrency

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit

/**
 * Kotlin 协程高阶并发实战：对比 Java 解决相同问题的方案
 */
object CoroutinesAdvancedLab {

    private const val TAG = "CoroutinesAdvanced"

    // =================================================================================
    // 场景 1: 原子性与 Mutex (互斥锁)
    // 解决：多协程并发修改共享变量
    // =================================================================================
    private var count = 0
    private val mutex = Mutex()

    private fun testMutex() = runBlocking {
        count = 0 // 重置计数器
        println("\n--- 1. Testing Mutex (vs Synchronized) ---")
        val jobs = List(100) {
            launch(Dispatchers.Default) {
                repeat(1000) {
                    // mutex.withLock 是挂起函数，而 synchronized 是阻塞的。
                    // 它可以保证同一时间只有一个协程进入临界区，但不会阻塞底层线程。
                    mutex.withLock {
                        count++
                    }
                }
            }
        }
        jobs.forEach { it.join() }
        println("[$TAG] Mutex Result: $count (Expected 100000)")
    }

    // =================================================================================
    // 场景 2: 任务并行与结果合并 (async/await)
    // 解决：类似 CompletableFuture 的任务编排
    // =================================================================================
    private fun testAsyncAwait() = runBlocking {
        println("\n--- 2. Testing async/await (vs CompletableFuture) ---")
        
        val startTime = System.currentTimeMillis()
        
        // 并行启动两个耗时任务
        val userDeferred = async(Dispatchers.IO) {
            delay(200)
            "User(Lucky)"
        }
        
        val configDeferred = async(Dispatchers.IO) {
            delay(300)
            "Config(Dark Mode)"
        }

        // 像写同步代码一样等待结果
        // 这比 CompletableFuture 的 thenCombine 更加直观
        val result = "Merged: ${userDeferred.await()} with ${configDeferred.await()}"
        
        println("[$TAG] Result: $result (Cost: ${System.currentTimeMillis() - startTime} ms)")
    }

    // =================================================================================
    // 场景 3: 管道通信 (Channel)
    // 解决：生产者-消费者模型 (vs BlockingQueue)
    // =================================================================================
    private fun testChannel() = runBlocking {
        println("\n--- 3. Testing Channel (vs BlockingQueue) ---")
        val channel = Channel<Int>(capacity = 2) // 缓冲区为 2

        // 生产者协程
        launch {
            for (x in 1..5) {
                println("[$TAG] Sending $x")
                channel.send(x) // 如果缓冲区满了，协程会挂起，不卡死线程
            }
            channel.close()
        }

        // 消费者协程
        launch {
            for (y in channel) {
                println("[$TAG] Received $y")
                delay(200) // 模拟消费较慢
            }
        }
    }

    // =================================================================================
    // 场景 4: 并发量限制 (Semaphore)
    // 解决：资源限流
    // =================================================================================
    private fun testSemaphore() = runBlocking {
        println("\n--- 4. Testing Coroutine Semaphore (Permit: 2) ---")
        val semaphore = Semaphore(2)

        val jobs = List(5) { id ->
            launch {
                semaphore.withPermit {
                    println("[$TAG] Worker $id acquired permit.")
                    delay(300)
                    println("[$TAG] Worker $id releasing.")
                }
            }
        }
        jobs.forEach { it.join() }
    }

    fun runTests() {
        println("========== Coroutines Advanced Tests Start ==========")
        testMutex()
        testAsyncAwait()
        testChannel()
        testSemaphore()
        println("========== Coroutines Advanced Tests End ==========\n")
    }
}
