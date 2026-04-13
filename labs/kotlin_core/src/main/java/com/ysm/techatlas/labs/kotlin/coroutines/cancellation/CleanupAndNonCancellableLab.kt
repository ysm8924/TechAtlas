package com.ysm.techatlas.labs.kotlin.coroutines.cancellation

import kotlinx.coroutines.*

/**
 * 【协程取消 03】：资源清理与 NonCancellable
 *
 * 【核心概念】：
 * 1. 当协程被取消时，我们通常在 `finally` 代码块中释放资源（关闭文件、断开连接等）。
 * 2. 但是，**已被取消的协程不允许再调用挂起函数！** 如果在 finally 里调用 `delay` 或其他挂起函数，会立即抛出 CancellationException，导致收尾工作中断。
 * 3. 破局之法：使用 `withContext(NonCancellable)`，它提供了一个“不可取消”的上下文，专门用来兜底做挂起式的收尾。
 *
 * 【面试考点】：
 * - 问：协程取消后，如何在 finally 中执行耗时的挂起操作（如通知服务器用户已下线）？
 * - 答：将代码包裹在 `withContext(NonCancellable) { ... }` 中。
 */
object CleanupAndNonCancellableLab {
    fun runDemo() = runBlocking {
        println("\n=== 03. 资源清理与 NonCancellable ===")

        val job = launch {
            try {
                println("  [Task] 开始执行，假装持有了一个数据库锁...")
                delay(1000)
            } finally {
                println("  [Task-Finally] 协程已结束（或被取消），开始释放锁...")
                
                // 演示：强制执行不可取消的挂起清理逻辑
                withContext(NonCancellable) {
                    println("    [NonCancellable] 正在进行耗时的数据库落盘收尾工作...")
                    delay(500) // 这里如果没包 NonCancellable，delay 会直接炸掉！
                    println("    [NonCancellable] 收尾工作完美结束！锁已释放。")
                }
            }
        }
        
        delay(200)
        println("  [Main] 取消任务...")
        job.cancelAndJoin()
        println("  [Main] 主流程完成。")
    }
}
