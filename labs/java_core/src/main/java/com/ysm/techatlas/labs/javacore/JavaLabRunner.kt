package com.ysm.techatlas.labs.javacore

import com.ysm.techatlas.labs.javacore.concurrency.ConcurrencyBasicsLab
import com.ysm.techatlas.labs.javacore.concurrency.ConcurrencyInterviewLab
import com.ysm.techatlas.labs.javacore.concurrency.ConcurrencyAdvancedLab

/**
 * Java 核心实验室运行入口
 */
fun main() {
    println(">>> Starting Java Labs Execution...\n")

    // 1. 并发进阶 (BlockingQueue, ReadWriteLock, Semaphore, CompletableFuture)
    ConcurrencyAdvancedLab.runTests()

    // 2. 并发高频面试题 (死锁、ABC、线程池验证)
    // ConcurrencyInterviewLab.runTests()

    // 3. 并发基础 (可见性、原子性) 测试
    // ConcurrencyBasicsLab.runTests()

    println("\n>>> Java Labs Execution Finished.")
}