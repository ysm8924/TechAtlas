package com.ysm.techatlas.labs.kotlin.coroutines.functions.scope

import kotlinx.coroutines.*

/**
 * 【函数名称】：withContext
 * 【作用分类】：上下文切换
 *
 * 【日常使用】：
 * 用于在同一个协程中**临时切换线程池 (Dispatcher)**，执行完指定代码块后，自动切回到原来的线程。
 * 极常用于：在主线程 (Main) 调用网络请求时，使用 `withContext(Dispatchers.IO)` 切到后台，请求完毕后拿着结果自动回到主线程更新 UI。
 *
 * 【掌握要点】：
 * 1. 它是**挂起函数**，不是协程启动器！它不创建新的协程。
 * 2. 它会阻塞当前协程的向下执行，直到其代码块返回结果。
 *
 * 【注意事项 & 面试常考点】：
 * - **面试题**：withContext 会创建新的协程吗？
 *   答：不会。它复用当前协程，只是改变了当前协程执行的上下文（主要是调度器）。这使得其性能开销极低。
 */
object WithContextLab {
    fun runDemo() = runBlocking {
        println("\n=== [WithContext Lab] ===")
        println("  Step 1: 当前执行线程: ${Thread.currentThread().name}")

        // 假设这里是主线程，我们需要去读写文件，临时切换到 IO 线程
        val result = withContext(Dispatchers.IO) {
            println("  Step 2 (withContext): 正在后台进行繁重工作... 所在线程: ${Thread.currentThread().name}")
            delay(100) // 模拟耗时
            "IO_Result_Data" // 隐式返回最后一行作为结果
        }

        println("  Step 3: 获取到了结果 [$result]，自动切回了原线程: ${Thread.currentThread().name}")
    }
}
