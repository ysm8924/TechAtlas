package com.ysm.techatlas.labs.kotlin.coroutines.functions.control

import kotlinx.coroutines.*

/**
 * 【函数名称】：repeat
 * 【作用分类】：流程控制函数 (Kotlin 标准库 inline 函数)
 *
 * 【日常使用】：
 * 结合协程，常常用于快速编写：轮询请求、心跳发送、测试数据的批量生成等。
 *
 * 【掌握要点】：
 * 它虽然不是协程专属的 API（它是 Kotlin 标准库的普通高阶内联函数），但是配合协程的 `delay` 食用极其方便。
 * `repeat(times)` 相当于传统的 `for (i in 0 until times)`。
 */
object RepeatLab {
    fun runDemo() = runBlocking {
        println("\n=== [Repeat Lab] ===")
        
        // 演示：模拟发送 3 次心跳包
        println("  -> 开始发送心跳包...")
        
        val job = launch {
            repeat(3) { index ->
                println("    ❤️ Heartbeat ${index + 1}")
                delay(200) // 每次心跳间隔 200ms
            }
        }
        
        job.join()
        println("  -> 心跳发送结束")
    }
}
