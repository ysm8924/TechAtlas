package com.ysm.techatlas.labs.kotlin.coroutines.functions.builders

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * 【函数名称】：runBlocking
 * 【作用分类】：协程启动函数 (桥接函数)
 *
 * 【日常使用】：
 * 1. 在 `main` 函数中作为入口，防止 JVM 在协程还没跑完时就退出了。
 * 2. 在**单元测试**中，用于测试挂起函数。
 *
 * 【掌握要点】：
 * 它是**阻塞的**！它会卡住当前调用的线程，直到它内部所有的协程（包括子协程）全部执行完毕。
 * 这与 `launch` 和 `async` 的“非阻塞（挂起）”有着本质的区别。
 *
 * 【注意事项 & 面试常考点】：
 * - **禁忌**：绝对不要在 Android 的主线程（UI 线程）中调用 `runBlocking`，否则会直接引发 ANR（Application Not Responding）。
 * - **面试题**：如果在 runBlocking 内部 delay 会怎样？
 *   答：delay 是挂起函数，会让出线程，但因为 runBlocking 的使命就是“阻塞当前线程等待协程结束”，所以外部的线程依然处于阻塞等待状态。
 */
object RunBlockingLab {
    fun runDemo() {
        println("\n=== [RunBlocking Lab] ===")
        println("  [Main] 开始执行，当前线程: ${Thread.currentThread().name}")
        
        val time = measureTimeMillis {
            // 这里会死死卡住当前线程（比如 main 线程）
            runBlocking {
                println("  [runBlocking] 内部开始，当前线程: ${Thread.currentThread().name}")
                
                launch {
                    delay(500)
                    println("  [runBlocking -> launch] 子协程执行完毕！")
                }
            }
        }
        
        println("  [Main] runBlocking 结束了，主线程才得以继续。耗时: ${time}ms")
    }
}
