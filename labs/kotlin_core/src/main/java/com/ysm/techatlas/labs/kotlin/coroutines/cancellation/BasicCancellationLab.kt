package com.ysm.techatlas.labs.kotlin.coroutines.cancellation

import kotlinx.coroutines.*

/**
 * 【协程取消 01】：基础取消与 CancellationException
 *
 * 【核心概念】：
 * 1. 协程的取消通过调用 `job.cancel()` 实现。
 * 2. 协程在被取消时，会在内部的**挂起点**抛出 `CancellationException`。
 * 3. 这是一个特殊的异常，协程系统认为它是“正常的任务结束”，不会导致程序崩溃。
 *
 * 【面试考点】：
 * - 问：协程抛出 CancellationException 会导致外层作用域崩溃吗？
 * - 答：不会。它是静默处理的，专门用于打断协程逻辑。如果在 try-catch 中捕获了它，最好选择重新抛出，或者不要滥用 catch(e: Exception) 把所有异常都吞掉，这会导致协程无法正常结束！
 */
object BasicCancellationLab {
    fun runDemo() = runBlocking {
        println("\n=== 01. 基础取消 (Basic Cancellation) ===")
        val job = launch {
            try {
                println("  [Task] 开始执行并挂起等待...")
                delay(1000)
                println("  [Task] 这行代码永远不会打印，因为上方挂起时被取消了")
            } catch (e: CancellationException) {
                // 建议：如果你只是想做清理，推荐用 finally；如果一定要 catch，一定要认识到这是 CancellationException。
                println("  [Task] 捕获到了取消异常: ${e.message}")
            }
        }
        
        delay(200) // 让协程跑一会儿
        println("  [Main] 取消任务...")
        job.cancel(CancellationException("外部用户主动取消任务")) // 可以携带自定义信息
        job.join()
        println("  [Main] 任务已确认取消完毕。")
    }
}
