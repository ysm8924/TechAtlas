package com.ysm.techatlas.labs.kotlin.coroutines

import kotlinx.coroutines.*

/**
 * 协程深入实验室：CPS 转换与状态机深度观察
 *
 * 目标：通过这个类的代码，专门供你使用 Android Studio 的反编译工具，
 * 去观察 Kotlin 编译器是如何将我们写的同步风格的挂起函数，转换为底层复杂的状态机机制的。
 */
object CpsAndStateMachineLab {

    fun runTests() {
        println("\n=== [Coroutines Lab] 02. CPS & State Machine Observation ===")
        
        // 桥接函数，阻塞主线程以等待内部协程执行完毕
        runBlocking {
            println("[runBlocking] Thread: ${Thread.currentThread().name}")
            
            // 1. 使用 launch 启动一个协程 (这会创建一个全新的 Continuation 实例作为起点)
            val job = launch {
                println("[launch] Coroutine started. Thread: ${Thread.currentThread().name}")
                
                // 2. 调用一个外部的挂起函数
                val finalResult = executeComplexTask()
                
                println("[launch] Final Result: $finalResult")
            }
            job.join()
        }
        println("==============================================================\n")
    }

    /**
     * 核心观察对象：含有多个挂起点（Suspend Points）的挂起函数。
     *
     * 【你可以这样去观察它的底层】：
     * 1. 鼠标点击这个函数名 `executeComplexTask`。
     * 2. 菜单栏：Tools -> Kotlin -> Show Kotlin Bytecode。
     * 3. 在弹出的窗口点击右上角的 "Decompile"。
     * 
     * 【反编译后的 Java 伪代码结构预告】：
     * 这个函数会被编译为一个形如 `Object executeComplexTask(Continuation var0)` 的方法。
     * 内部会自动生成一个实现了 Continuation 接口的匿名内部类（其实就是状态机 StateMachine）。
     * 代码逻辑会被一个 switch(label) 包裹：
     * - case 0: 初始执行第一段逻辑，然后把 label 改为 1，调用 fetchRemoteData。若返回挂起标志，立刻 return 释放线程。
     * - case 1: 恢复执行，拿到 data1，执行第二段逻辑。把 label 改为 2，再次调用 fetchRemoteData。若返回挂起，再次 return。
     * - case 2: 恢复执行，拿到 data2，合并结果并 return。
     */
    private suspend fun executeComplexTask(): String {
        println("[executeComplexTask] Step 1: Ready to fetch Data A")
        
        // 挂起点 1：编译器会在这里切断代码，上半部分是 case 0，下半部分是 case 1
        val data1 = fetchRemoteData("A")
        
        println("[executeComplexTask] Step 2: Got $data1, now fetch Data B")
        
        // 挂起点 2：编译器再次切断代码，下半部分成为 case 2
        val data2 = fetchRemoteData("B")
        
        println("[executeComplexTask] Step 3: All data fetched!")
        
        return "$data1 & $data2"
    }

    /**
     * 内部继续调用的挂起函数
     *
     * 同样，编译后也会多出一个 Continuation 参数。
     */
    private suspend fun fetchRemoteData(id: String): String {
        println("[fetchRemoteData] Fetching $id ... (Will suspend for 200ms)")
        
        // delay 也是挂起函数，真正触发线程释放的地方
        delay(200) 
        
        println("[fetchRemoteData] Fetching $id completed.")
        return "Data-$id"
    }
}
