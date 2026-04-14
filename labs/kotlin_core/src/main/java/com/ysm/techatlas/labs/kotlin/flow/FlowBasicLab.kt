package com.ysm.techatlas.labs.kotlin.flow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

/**
 * 冷流 (Cold Flow) 基础与核心操作符验证室
 */
object FlowBasicLab {

    @PublishedApi
    internal const val TAG = "FlowBasicLab"

    // =================================================================================
    // 考点 1: 冷流的特性 (不调用 collect 绝不执行)
    // =================================================================================
    private fun makeColdFlow(): Flow<Int> = flow {
        println("[$TAG] Flow started! (If you don't collect me, you won't see this)")
        for (i in 1..3) {
            delay(100)
            println("[$TAG] Emitting $i")
            emit(i)
        }
    }



    private fun testColdFlow() = runBlocking {
        println("\n--- Testing Cold Flow ---")
        val myFlow = makeColdFlow()
        println("[$TAG] Flow created, but not collected yet.")
        
        // 第一次收集，会触发内部代码执行
        println("[$TAG] 1st Collect:")
        myFlow.collect {
            delay(300) // 模拟耗时操作
            println("[$TAG] Collected $it")
        }

        // 第二次收集，会【再次】触发完整执行！
        println("[$TAG] 2nd Collect:")
        myFlow.collect {
            delay(200) // 模拟耗时操作
            println("[$TAG] Collected $it")
        }
    }

    // =================================================================================
    // 考点 2: Context 保护与 flowOn
    // 面试必考：如何让流在 IO 线程生产，在 Main 线程消费？
    // =================================================================================
    private fun testFlowOn() = runBlocking {
        println("\n--- Testing flowOn Context ---")
        flow {
            val threadName = Thread.currentThread().name
            println("[$TAG] Emitting on: $threadName")
            emit("Data")
            // withContext(Dispatchers.IO) { emit("Error") } // 【绝对禁止】Flow 中不能用 withContext 切换发射线程！
        }
        .map { 
            println("[$TAG] Mapping on: ${Thread.currentThread().name}")
            "Mapped $it"
        }
        // flowOn 会影响它【上方】所有操作符和数据源的运行线程！
        .flowOn(Dispatchers.IO) 
        .collect {
            // collect 默认运行在调用它的协程上下文中（这里是 runBlocking 的主协程）
            println("[$TAG] Collected on: ${Thread.currentThread().name}")
        }
    }




    // =================================================================================
    // 考点 3: 背压 (Backpressure) - 生产快，消费慢
    // =================================================================================
    private fun testBackpressure() = runBlocking {
        println("\n--- Testing Backpressure (conflate & collectLatest) ---")
        
        val time = System.currentTimeMillis()
        
        flow {
            for (i in 1..3) {
                delay(200) // 生产很快 (100ms)
                println("[$TAG] Emitting $i")
                emit(i)
            }
        }
        // 应对策略 1：buffer() -> 会缓存，生产者不等待，消费者慢慢消费，总时间等于消费总时间
        // 应对策略 2：conflate() -> 直接丢弃中间过时的数据，只消费最新的！
        .buffer()
        .collect {
            delay(100) // 消费很慢 (300ms)
            println("[$TAG] Done processing $it")
        }
        println("[$TAG] Total time: ${System.currentTimeMillis() - time} ms")
    }

    // ================== 测试入口 ==================
    fun runTests() {
        println("========== FlowBasicLab Tests Start ==========")
        testColdFlow()
        testFlowOn()
        testBackpressure()
        println("========== FlowBasicLab Tests End ==========\n")
    }
}