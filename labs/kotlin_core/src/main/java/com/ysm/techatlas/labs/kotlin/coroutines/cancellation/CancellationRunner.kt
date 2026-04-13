package com.ysm.techatlas.labs.kotlin.coroutines.cancellation

/**
 * 协程取消机制 (Cancellation) 章节集中运行入口
 */
object CancellationRunner {
    fun runAll() {
        println("========= 开始执行协程取消机制 (Cancellation) 解析 =========\n")
        
        BasicCancellationLab.runDemo()
        CooperativeCancellationLab.runDemo()
        CleanupAndNonCancellableLab.runDemo()
        TimeoutCancellationLab.runDemo()
        ParentChildCancellationLab.runDemo()
        
        println("\n========= 协程取消机制 执行完毕 =========")
    }
}
