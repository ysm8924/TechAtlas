package com.ysm.techatlas.labs.kotlin.coroutines.functions.lifecycle

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * 【函数名称】：delay
 * 【作用分类】：生命周期与状态控制函数
 *
 * 【日常使用】：
 * 等待一段时间。常用于模拟网络延迟、做动画间隔、或者写轮询时避开过于频繁的循环。
 *
 * 【掌握要点】：
 * 它会“挂起”协程，但不阻塞线程。底层原理其实是通过 `ScheduledExecutorService` 或事件循环设定一个未来的唤醒任务。
 *
 * 【注意事项 & 面试常考点】：
 * - **面试题**：`delay(100)` 和 `Thread.sleep(100)` 的区别是什么？
 *   答：`delay` 是非阻塞的，会让出当前线程去干别的活，100ms后带着状态再切回来；`Thread.sleep` 是阻塞的，线程直接睡死，别的任务也别想用这根线程。
 */
object DelayLab {
    fun runDemo() = runBlocking {
        println("\n=== [Delay Lab] ===")
        
        val time = measureTimeMillis {
            // 启动两个并发协程
            val job1 = launch {
                println("  [Task-1] 准备 delay 300ms... 释放线程")
                delay(300)
                println("  [Task-1] 恢复执行！")
            }
            val job2 = launch {
                println("  [Task-2] 准备 delay 200ms... 释放线程")
                delay(200)
                println("  [Task-2] 恢复执行！")
            }
            joinAll(job1, job2)
        }
        
        println("  总耗时: ${time}ms (说明两者是重叠并行的，没有互相阻塞线程睡死)")
    }
}
