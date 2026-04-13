package com.ysm.techatlas.labs.kotlin.coroutines.cancellation

import kotlinx.coroutines.*

/**
 * 【协程取消 04】：超时自动取消
 *
 * 【核心概念】：
 * 网络请求或业务操作必须要有时间限制。
 * - `withTimeout(time)`: 时间一到，内部抛出 `TimeoutCancellationException`。
 * - `withTimeoutOrNull(time)`: 时间一到，优雅中止并返回 `null`。
 *
 * 【注意点】：
 * 超时底层的原理依旧是 `cancel()`，因此它同样要求代码必须是**协作式的 (支持挂起点)**！
 * 遇到不响应 cancel 的死循环代码，withTimeout 也会束手无策！
 */
object TimeoutCancellationLab {
    fun runDemo() = runBlocking {
        println("\n=== 04. 超时自动取消 (withTimeout) ===")

        println("  [Demo] withTimeoutOrNull 测试 (限时 300ms)")
        val result = withTimeoutOrNull(300) {
            println("    [Task] 开始请求网络，模拟耗时 500ms...")
            delay(500)
            "返回的真实数据"
        }
        
        if (result == null) {
            println("  [Demo] 请求超时！返回了 null。")
        } else {
            println("  [Demo] 请求成功: $result")
        }
    }
}
