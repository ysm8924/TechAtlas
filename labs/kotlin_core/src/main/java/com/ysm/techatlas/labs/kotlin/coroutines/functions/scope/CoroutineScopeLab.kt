package com.ysm.techatlas.labs.kotlin.coroutines.functions.scope

import kotlinx.coroutines.*

/**
 * 【函数名称】：coroutineScope
 * 【作用分类】：作用域函数 (结构化并发)
 *
 * 【日常使用】：
 * 当你需要在一个挂起函数中，并行启动多个子任务，并且希望“只要有一个失败，整个任务就宣告失败并取消其他还在跑的任务”时使用。
 * 典型场景：登录时同时获取 A、B 两个前置配置，缺一不可。
 *
 * 【掌握要点】：
 * 1. 这是一个**挂起函数**，会等待其代码块内所有启动的子协程执行完毕后才恢复。
 * 2. **异常双向传播**：子协程崩溃 -> 取消父协程 -> 取消所有其他兄弟协程 -> 将异常抛给外部。
 *
 * 【注意事项 & 面试常考点】：
 * - **面试题**：它与刚才学的 supervisorScope 核心区别是什么？
 *   答：`coroutineScope` 是一损俱损（连带取消）；`supervisorScope` 是互不影响（子协程自己背锅）。
 */
object CoroutineScopeLab {
    fun runDemo() = runBlocking {
        println("\n=== [CoroutineScope Lab] ===")
        
        try {
            coroutineScope {
                val job1 = launch {
                    delay(500)
                    println("  [Task-1] 我是需要很长时间的任务，如果不出错，我会打印。")
                }

                val job2 = launch {
                    delay(100)
                    println("  [Task-2] 发生致命错误，准备崩溃！")
                    throw RuntimeException("Task-2 崩溃了")
                }
                
                job1.invokeOnCompletion { e ->
                    if (e is CancellationException) {
                        println("  [Task-1] 被迫取消了，因为兄弟协程炸了！异常原因: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            println("  [外部捕获] 捕获到了作用域抛出的异常: ${e.message}")
        }
    }
}
