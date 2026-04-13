package com.ysm.techatlas.labs.kotlin.coroutines.functions.lifecycle

import kotlinx.coroutines.*

/**
 * 【函数名称】：withTimeout / withTimeoutOrNull
 * 【作用分类】：生命周期与状态控制函数
 *
 * 【日常使用】：
 * 极其常用的“限时操作”利器。给某段可能无限挂起或耗时太长的操作（如网络请求、数据库查询）设置一个超时时间。
 *
 * 【掌握要点】：
 * - `withTimeout(time)`: 超时后直接抛出 `TimeoutCancellationException`。
 * - `withTimeoutOrNull(time)`: 超时后不抛异常，而是优雅地返回 `null`。通常推荐使用这个，能减少 try-catch 的样板代码。
 *
 * 【注意事项 & 面试常考点】：
 * - **底层原理**：它会在内部启动一个定时器，一旦超时，就会调用内部所在协程的 `cancel()`。
 * - **注意**：如果内部代码是“阻塞不可取消的”（如死循环且没有 yield），超时也无法打断它！
 */
object WithTimeoutLab {
    fun runDemo() = runBlocking {
        println("\n=== [WithTimeout Lab] ===")
        
        // 演示 1：withTimeoutOrNull 优雅处理超时
        println("  [Demo 1] 使用 withTimeoutOrNull (限时 300ms)")
        val result = withTimeoutOrNull(300L) {
            println("    -> 开始请求网络...")
            delay(500) // 模拟网络非常慢
            "网络数据"
        }
        println("  [Demo 1] 结果: ${result ?: "请求超时，返回了 null！"}")

        // 演示 2：withTimeout 抛出异常
        println("\n  [Demo 2] 使用 withTimeout (限时 300ms)")
        try {
            withTimeout(300L) {
                println("    -> 开始数据库查询...")
                delay(500) 
            }
        } catch (e: TimeoutCancellationException) {
            println("  [Demo 2] 捕获到异常: 超过时间限制！")
        }
    }
}
