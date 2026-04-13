package com.ysm.techatlas.labs.kotlin.coroutines.functions.builders

import kotlinx.coroutines.*

/**
 * 【函数名称】：launch
 * 【作用分类】：协程启动函数
 *
 * 【日常使用】：
 * 触发一个不需要返回结果的异步任务。例如：埋点上报、写本地日志、存入数据库。
 * 业界常称为 "Fire and forget" (发射后不管)。
 *
 * 【掌握要点】：
 * 1. 返回值是 `Job`，代表这个协程的句柄，可以通过它取消任务 (`job.cancel()`) 或等待完成 (`job.join()`)。
 * 2. 属于非阻塞式，不会卡住当前线程。
 *
 * 【注意事项 & 面试常考点】：
 * - **面试题**：launch 中抛出异常会怎样？
 *   答：默认情况下，`launch` 内部未捕获的异常会**立刻抛出**给所属的作用域，如果不做特殊处理（如配置 `CoroutineExceptionHandler`），会导致整个应用程序 Crash！
 */
object LaunchLab {
    fun runDemo() = runBlocking {
        println("\n=== [Launch Lab] ===")
        val job = launch {
            println("  [launch] 任务开始执行...")
            delay(100)
            println("  [launch] 任务执行完毕！")
            // 尝试取消注释下一行，体会异常是如何直接抛出的
            // throw RuntimeException("Launch 中发生崩溃！") 
        }
        
        println("  [runBlocking] launch 已经触发，继续执行主流程。")
        job.join() // 等待 launch 执行完毕，以保证输出完整
    }
}
