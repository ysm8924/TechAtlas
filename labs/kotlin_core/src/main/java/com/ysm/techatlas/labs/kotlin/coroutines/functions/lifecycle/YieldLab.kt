package com.ysm.techatlas.labs.kotlin.coroutines.functions.lifecycle

import kotlinx.coroutines.*

/**
 * 【函数名称】：yield
 * 【作用分类】：生命周期与状态控制函数
 *
 * 【日常使用】：
 * 场景：如果你有一个非常密集计算的死循环/大循环（比如解析巨大的本地 JSON 或压缩图片），
 * 它占用了 CPU 导致其他协程饿死，或者导致你无法取消它。此时需要时不时调用 `yield()`。
 *
 * 【掌握要点】：
 * 1. **让出执行权**：`yield()` 会暂停当前协程，将线程池的执行权交出，让其他排队的协程先跑，然后再排队恢复自己。
 * 2. **检查取消状态**：挂起函数被调用时，都会自动检查当前协程是否已经被 `cancel`。因此 `yield()` 常用来配合长耗时计算实现“可响应取消”。
 *
 * 【注意事项 & 面试常考点】：
 * - **面试题**：协程调用 cancel() 之后，里面的代码一定会停下吗？
 *   答：不一定。协程的取消是**“协作式 (Cooperative)”**的。如果在计算密集型的循环里没有调用任何挂起函数（如 delay, yield，或没检查 isActive），它会一直跑到循环结束！
 */
object YieldLab {
    fun runDemo() = runBlocking(Dispatchers.Default) {
        println("\n=== [Yield Lab] ===")
        val job = launch {
            println("  [Task] 开始执行一个耗时的死循环...")
            var count = 0
            while (count < 5) {
                count++
                println("  [Task] 计算中... $count")
                
                // 模拟耗时的 CPU 计算 (Thread.sleep 阻塞而非挂起)
                Thread.sleep(100) 
                
                // 【核心所在】：每次循环检查并让出。
                // 若被取消，这里会抛出 CancellationException 从而中断循环！
                yield() 
            }
            println("  [Task] 循环自然结束！(如果你看到这句话说明取消失败)")
        }

        delay(250) // 主流程等待一会儿
        println("  [Main] 取消任务...")
        job.cancelAndJoin()
        println("  [Main] 取消成功！")
    }
}
