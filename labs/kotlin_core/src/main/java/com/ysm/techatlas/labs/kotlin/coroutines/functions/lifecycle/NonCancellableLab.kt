package com.ysm.techatlas.labs.kotlin.coroutines.functions.lifecycle

import kotlinx.coroutines.*

/**
 * 【函数名称】：NonCancellable (配合 withContext 使用)
 * 【作用分类】：生命周期与状态控制函数 (逃避取消)
 *
 * 【日常使用】：
 * 场景：协程被意外取消了（或者发生了异常），但是在 finally 块中，你还需要做一些**收尾的挂起操作**。
 * 例如：关闭网络流、写入崩溃日志到本地文件 (这需要挂起耗时)。
 * 如果你在已被取消的协程的 finally 块里直接调用 `delay` 或 `挂起写文件`，它会立即抛出 `CancellationException` 导致收尾失败。
 * 此时需要用 `withContext(NonCancellable)` 强行在取消状态下续命执行。
 *
 * 【掌握要点】：
 * `NonCancellable` 本质上是一个没有生命周期绑定、永远处于 isActive=true 的 Context 元素。
 *
 * 【注意事项 & 面试常考点】：
 * - **面试题**：如何在协程被取消后，依然保证能够执行耗时的挂起收尾操作？
 *   答：在 finally 块中，使用 `withContext(NonCancellable) { ... }` 包裹收尾逻辑。
 */
object NonCancellableLab {
    fun runDemo() = runBlocking {
        println("\n=== [NonCancellable Lab] ===")
        val job = launch {
            try {
                println("  [Task] 开始工作...")
                delay(1000)
            } catch (e: CancellationException) {
                println("  [Task] 被取消了！原因: ${e.message}")
            } finally {
                // 如果这里不加 withContext(NonCancellable)，delay 会立刻失败抛出异常
                withContext(NonCancellable) {
                    println("  [Task-Finally] 正在执行重要的收尾工作 (受 NonCancellable 保护)...")
                    delay(500) // 收尾工作需要挂起耗时
                    println("  [Task-Finally] 收尾工作完成。")
                }
            }
        }
        
        delay(200) // 让它启动
        println("  [Main] 取消任务...")
        job.cancel(CancellationException("外部用户手动取消"))
    }
}
