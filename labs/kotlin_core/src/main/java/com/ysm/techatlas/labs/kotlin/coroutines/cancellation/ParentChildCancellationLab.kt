package com.ysm.techatlas.labs.kotlin.coroutines.cancellation

import kotlinx.coroutines.*

/**
 * 【协程取消 05】：父子协程取消的传播特性 (结构化并发)
 *
 * 【核心概念 - 取消的单向传播】：
 * 1. **向下传播**：当父协程被取消时，它会自动取消其所有的子协程。
 * 2. **不可向上波及**：当子协程被**正常取消 (CancellationException)** 时，它不会波及父协程和其他兄弟协程。(注意区分：如果是崩溃异常 Exception，是会波及的，除非用 SupervisorJob)
 *
 * 【面试考点】：
 * - 问：子协程手动调用 cancel() 结束自己，父协程会被干掉吗？
 * - 答：不会。`CancellationException` 被认为是正常的结束信号，父协程仅会把它从子任务列表中剔除。
 */
object ParentChildCancellationLab {
    fun runDemo() = runBlocking {
        println("\n=== 05. 父子层级传播 (Parent-Child Propagation) ===")

        val parentJob = launch {
            println("  [Parent] 启动了...")
            
            val child1 = launch {
                println("    [Child-1] 运行中...")
                delay(1000)
                println("    [Child-1] 执行完毕 (你看不见这行因为我被连坐了)")
            }
            
            val child2 = launch {
                println("    [Child-2] 运行中，但我即将自尽...")
                delay(100)
                // 子任务主动取消自己，抛出 CancellationException
                cancel() 
            }
            
            // 监控 child2 结束
            child2.invokeOnCompletion { e ->
                println("    [Child-2] 已死亡。是否是因为取消？${e is CancellationException}")
            }
            
            delay(300)
            println("  [Parent] Child-2死了我依然好好的。现在我要自尽了，Child-1 也会跟着我死！")
        }
        
        delay(400) // 让剧情发展
        parentJob.cancelAndJoin()
        println("  [Main] 整个家族已覆灭。")
    }
}
