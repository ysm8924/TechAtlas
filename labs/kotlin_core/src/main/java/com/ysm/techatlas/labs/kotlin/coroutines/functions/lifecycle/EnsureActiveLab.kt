package com.ysm.techatlas.labs.kotlin.coroutines.functions.lifecycle

import kotlinx.coroutines.*

/**
 * 【函数名称】：ensureActive() / isActive
 * 【作用分类】：生命周期与状态控制函数 (取消机制协作)
 *
 * 【日常使用】：
 * 场景：我们在协程里执行大量的文件读写、复杂的数学运算等**没有调用挂起函数 (delay/yield)** 的操作。
 * 当外界调用 `job.cancel()` 时，这种密集计算型协程是**不会立刻停止**的，因为它没有检查点！
 * 此时我们需要手动在每次循环中调用 `ensureActive()`。
 *
 * 【掌握要点】：
 * - `isActive` (属性): 返回当前协程是否处于存活状态。常用于 `while(isActive) { ... }`。
 * - `ensureActive()` (函数): 检查状态，如果发现协程被取消了，直接抛出 `CancellationException` 打断执行。
 *
 * 【注意事项 & 面试常考点】：
 * - **极其经典的面试题**：“我调用了 job.cancel()，为什么协程还在跑？”
 *   答：因为协程的取消是**协作式的 (Cooperative)**。如果协程内部都在执行普通 CPU 密集型计算，且没有抛出 CancellationException 的挂起点（如 delay），它会一直跑到执行结束。必须手动调用 `ensureActive()` 检查并响应取消。
 */
object EnsureActiveLab {
    fun runDemo() = runBlocking(Dispatchers.Default) {
        println("\n=== [EnsureActive / isActive Lab] ===")
        
        val job = launch {
            println("  [Task] 开始执行繁重的 CPU 密集型任务...")
            var count = 0
            val startTime = System.currentTimeMillis()
            
            // 尝试将 `isActive` 换成 `true`，你会发现任务无法被取消！
            while (isActive) {
                // 也可以用： ensureActive() // 被取消时立刻抛出异常
                
                count++
                if (count % 1_0000_0000 == 0) {
                    println("  [Task] 已计算 $count 次...")
                }
            }
            println("  [Task] 循环结束，耗时: ${System.currentTimeMillis() - startTime}ms")
        }

        delay(10) // 让子任务跑一会儿
        println("  [Main] 发送取消信号...")
        job.cancelAndJoin()
        println("  [Main] 任务确认已停止。")
    }
}
