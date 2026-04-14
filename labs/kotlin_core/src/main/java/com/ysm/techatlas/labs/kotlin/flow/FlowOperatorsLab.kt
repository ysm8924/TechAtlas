package com.ysm.techatlas.labs.kotlin.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

/**
 * Flow 核心操作符与高频面试题实战验证室
 */
object FlowOperatorsLab {

    @PublishedApi
    internal const val TAG = "FlowOperatorsLab"

    // =================================================================================
    // 考点 1: map 与万能的 transform
    // =================================================================================
    private fun testTransform() = runBlocking {
        println("\n--- Testing map vs transform ---")
        
        val numbers = flowOf(1, 2, 3)

        // 1. map 是一对一的转换
        numbers.map { "Mapped $it" }.collect { println("[$TAG] $it") }

        // 2. transform 是万能的，它可以一对多，也可以做过滤！
        numbers.transform { value ->
            if (value % 2 != 0) { // 类似 filter 的功能
                emit("Transformed Start: $value") // 可以发一次
                emit("Transformed End: $value")   // 可以发多次！这是 map 做不到的
            }
        }.collect {
            println("[$TAG] $it")
        }
    }

    // =================================================================================
    // 考点 2: 终端操作符 reduce vs fold
    // =================================================================================
    private fun testReduceAndFold() = runBlocking {
        println("\n--- Testing reduce vs fold ---")
        
        val numbers = flowOf(1, 2, 3, 4, 5)

        // reduce: 把第一个值当初始值，依次往后累加
        val sum = numbers.reduce { accumulator, value -> accumulator + value }
        println("[$TAG] Reduce Sum: $sum")

        // fold: 给定一个初始值 100，再开始累加
        val foldSum = numbers.fold(100) { accumulator, value -> accumulator + value }
        println("[$TAG] Fold Sum: $foldSum")

        // 【面试坑点验证】：如果流是空的会怎样？
        val emptyFlow = emptyFlow<Int>()
        
        try {
            emptyFlow.reduce { acc, v -> acc + v }
        } catch (e: Exception) {
            println("[$TAG] Caught reduce error: ${e.message} (Because flow is empty)")
        }

        val safeFold = emptyFlow.fold(100) { acc, v -> acc + v }
        println("[$TAG] Safe Fold result on empty flow: $safeFold")
    }

    // =================================================================================
    // 考点 3: zip (严格拉链) vs combine (最新值组合)
    // =================================================================================
    private fun testZipVsCombine() = runBlocking {
        println("\n--- Testing zip vs combine ---")

        // Flow A 节奏：100, 200, 300 ms 发射 1, 2, 3
        val flowA = flow {
            delay(100); emit(1)
            delay(100); emit(2)
            delay(100); emit(3)
        }

        // Flow B 节奏：150, 300, 450 ms 发射 "A", "B", "C"
        val flowB = flow {
            delay(150); emit("A")
            delay(150); emit("B")
            delay(150); emit("C")
        }

        println(">> ZIP Test (Strict 1:1 Pairing)")
        // Zip 必须等双方各出一个值，配成一对才下发
        flowA.zip(flowB) { a, b -> "$a -> $b" }.collect {
            println("[$TAG] Zip Output: $it")
        }
        // Zip 输出: (1->A), (2->B), (3->C)

        println(">> COMBINE Test (Latest Values)")
        // Combine 不等人！只要自己有新值，就抓对方现存最新的值凑对
        flowA.combine(flowB) { a, b -> "$a -> $b" }.collect {
            println("[$TAG] Combine Output: $it")
        }
        // Combine 详细时间轴推理：
        // 100ms: A 发 1，B 没发，等待。
        // 150ms: B 发 "A"，A 最新是 1 -> 输出 "1 -> A"
        // 200ms: A 发 2，B 最新是 "A" -> 输出 "2 -> A"
        // 300ms: A 发 3，B 发 "B"。两者几乎同时更新，根据内部挂起调度，组合输出 "3 -> A"(或B) 和 "3 -> B"
        // 450ms: B 发 "C"，A 最新是 3 -> 输出 "3 -> C"
    }

    // =================================================================================
    // 考点 4: 时间控制 - debounce (防抖)
    // =================================================================================
    @OptIn(kotlinx.coroutines.FlowPreview::class)
    private fun testDebounce() = runBlocking {
        println("\n--- Testing debounce ---")
        
        flow {
            emit("K")
            delay(50) // 等 50ms 
            emit("Ko")
            delay(50) 
            emit("Kot")
            
            // 突然停顿了 200ms！
            delay(200) 
            
            emit("Kotlin")
        }
        .debounce(100) // 规则：如果 100ms 内有新数据来，就抛弃旧的；如果熬过了 100ms 还没新数据，才把旧的放行。
        .collect {
            println("[$TAG] Debounced Result: $it")
        }
        // 结果只会打印 "Kot" (因为后面停顿了 200ms > 100ms) 和 "Kotlin" (最后一个元素自然放出)
    }

    // ================== 测试入口 ==================
    fun runTests() {
        println("========== FlowOperatorsLab Tests Start ==========")
        testTransform()
        testReduceAndFold()
        testZipVsCombine()
        testDebounce()
        println("========== FlowOperatorsLab Tests End ==========\n")
    }
}