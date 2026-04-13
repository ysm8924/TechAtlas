package com.ysm.techatlas.labs.kotlin.coroutines.cancellation

import kotlinx.coroutines.*

/**
 * 【协程取消 02】：协作式取消 (极其重要的高频面试点)
 *
 * 【核心概念】：
 * 协程的取消是**协作式的 (Cooperative)**。
 * 调用 `job.cancel()` 仅仅是将协程的状态标记为“已取消 (isCancelled = true)”，
 * 只有当协程内部调用了标准库的**挂起函数 (如 delay, yield 等)**，或者手动检查了状态时，它才会真正抛出异常并停下。
 *
 * 【面试考点】：
 * - 问：为什么我取消了协程，它还在疯狂消耗 CPU 继续运行？
 * - 答：因为里面是一个 CPU 密集型循环（如文件/图片处理），并且**没有挂起点**去检查取消状态。
 * - 解决办法：
 *   1. 使用 `isActive` (属性) 在 while 中作为条件判断。
 *   2. 使用 `ensureActive()` (函数) 主动抛出取消异常。
 *   3. 适当调用 `yield()` 让出线程并检查取消状态。
 */
object CooperativeCancellationLab {
    fun runDemo() = runBlocking(Dispatchers.Default) {
        println("\n=== 02. 协作式取消 (Cooperative Cancellation) ===")

        val job = launch {
            println("  [Task] 开始死循环计算...")
            var i = 0
            val startTime = System.currentTimeMillis()
            
            // 错误示范：如果用 while (i < 5)，它将无法被外部取消！
            // 正确示范：使用 isActive 检查
            while (isActive) {
                // 模拟耗时 CPU 计算（注意：这里用的 sleep 阻塞而非挂起）
                Thread.sleep(100)
                i++
                println("  [Task] 计算中... $i")
                
                // 另一种正确方式：在循环体内部调用 ensureActive() 
                // ensureActive()
                
                if (i >= 5) break // 防止控制台无限打印
            }
            println("  [Task] 循环结束，耗时 ${System.currentTimeMillis() - startTime}ms")
        }

        delay(250) // 等待 250ms 后发出取消指令
        println("  [Main] 发出取消信号...")
        job.cancelAndJoin()
        println("  [Main] 取消流程结束。如果上面的循环能停留在'3'左右，说明协作取消成功！")
    }
}
