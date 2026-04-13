package com.ysm.techatlas.labs.kotlin.coroutines.functions.builders

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * 【函数名称】：async & await
 * 【作用分类】：协程启动函数
 *
 * 【日常使用】：
 * 当你需要并发执行多个任务，并且**需要它们的结果**来进行下一步操作时使用。
 * 例如：同时请求“用户信息”和“账户余额”两个接口，等两个都返回后刷新 UI。
 *
 * 【掌握要点】：
 * 1. 返回值是 `Deferred<T>`（它是 `Job` 的子接口，带泛型），通过 `.await()` 获取最终结果。
 * 2. `await()` 是一个挂起函数。
 *
 * 【注意事项 & 面试常考点】：
 * - **面试题**：launch 和 async 的核心区别是什么？
 *   答：① 返回值不同：Job vs Deferred；② **异常抛出时机不同**：`async` 内部抛出异常时，默认会被静默封装在 Deferred 里，只有当你调用 `await()` 去获取结果时，异常才会被抛出供外部 try-catch！
 * - **陷阱**：不要用多个 `await()` 串行写，要一起并发再 `await`（见示例）。
 */
object AsyncLab {
    fun runDemo() = runBlocking {
        println("\n=== [Async Lab] ===")
        
        val time = measureTimeMillis {
            // 错误用法演示（串行）：
            // val res1 = async { fetchValue1() }.await()
            // val res2 = async { fetchValue2() }.await() 
            // 👆 上面这种写法跟不用 async 没区别，失去了并发意义。

            // 正确用法演示（并发）：
            val deferred1 = async { fetchValue(1, 200) }
            val deferred2 = async { fetchValue(2, 300) }

            // 此时两个任务在同时进行
            val result1 = deferred1.await() // 挂起，等待1完成
            val result2 = deferred2.await() // 挂起，等待2完成
            
            println("  [Async] 最终结果拼接: $result1 & $result2")
        }
        println("  [Async] 两个任务耗时 ${time}ms (远小于 200+300=500ms，说明是并发)")
    }

    private suspend fun fetchValue(id: Int, time: Long): String {
        delay(time)
        return "Value_$id"
    }
}
