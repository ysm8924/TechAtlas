package com.ysm.techatlas.labs.kotlin.coroutines

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * 协程基础实验室 - 第一课：基础与原理解析
 */
object CoroutinesBasicLab {

    fun runTests() {
        println("=== [Coroutines Lab] 01. Basics & Suspend ===")
        
        // 1. 认识 runBlocking 和 launch
        testBasicBuilders()
        
        // 2. 挂起函数的本质与状态机 (详见注释和挂起函数)
        testSuspendFunction()
        
        println("==============================================\n")
    }

    /**
     * 知识点 1：协程构建器 (Builders)
     * - runBlocking: 阻塞当前线程，直到内部协程及所有子协程执行完毕。通常只用于 main 函数或测试中，桥接阻塞与非阻塞世界。
     * - launch: 非阻塞，不返回结果（返回 Job），即“发射后不管”。
     * - async: 非阻塞，返回 Deferred<T>（Job 的子类），可以通过 await() 获取结果。
     */
    private fun testBasicBuilders() {
        println("\n--- 1. testBasicBuilders ---")
        
        // 使用 runBlocking 阻塞当前主线程，等待内部执行完毕
        runBlocking {
            println("[${Thread.currentThread().name}] runBlocking starts")

            // launch 是在当前的 CoroutineScope 下启动一个新的子协程
            val job = launch {
                delay(100) // delay 是一个挂起函数，不会阻塞线程，而是挂起协程释放线程资源
                println("[${Thread.currentThread().name}] launch finishes")
            }

            // async 启动协程并返回结果
            val deferred = async {
                delay(200)
                "async result"
            }

            println("[${Thread.currentThread().name}] waiting for async result: ${deferred.await()}")
            job.join() // 等待 launch 结束
            println("[${Thread.currentThread().name}] runBlocking ends")
        }
    }

    /**
     * 知识点 2：挂起函数 (suspend) 的本质
     * 面试重点：什么是挂起？协程是如何实现挂起和恢复的？
     *
     * - 挂起 (Suspend)：指的是“协程”被挂起，暂停执行，让出它所在的“线程”。线程可以继续去执行其他的协程代码。
     * - 底层原理：CPS (Continuation-Passing Style，连续传递风格) 和 状态机。
     */
    private fun testSuspendFunction() = runBlocking {
        println("\n--- 2. testSuspendFunction ---")
        val time = measureTimeMillis {
            val result1 = fetchData(1)
            val result2 = fetchData(2)
            println("Results: $result1, $result2")
        }
        println("Total time (Sequential): $time ms")
    }

    /**
     * 深入理解 suspend 关键字：
     * 
     * 在编译后，这个函数的签名会变成类似这样（Java 伪代码）：
     * Object fetchData(int id, Continuation<String> continuation) { ... }
     * 
     * 1. Continuation：包含了恢复执行的上下文（包含当前的局部变量和接下来要执行的代码位置/状态标记）。
     * 2. 状态机：如果函数内有多个挂起调用（比如多次 delay），编译器会生成一个 switch-case 状态机。
     *    - case 0: 初始状态，执行第一段逻辑，把状态标记为 1，然后调用 delay。如果 delay 返回 COROUTINE_SUSPENDED，直接 return 退出当前函数（释放线程）。
     *    - case 1: delay 执行完后，底层通过 Continuation 的 resumeWith() 重新调用本函数，进入 case 1，恢复局部变量，继续向下执行。
     * 
     * 操作实践：你可以点击 Android Studio 菜单栏：
     * Tools -> Kotlin -> Show Kotlin Bytecode，然后在弹出的面板中点击 "Decompile"，
     * 即可看到这个被编译为状态机 (switch/label) 的真实 Java 代码。
     */
    private suspend fun fetchData(id: Int): String {
        println("[${Thread.currentThread().name}] Fetching data $id ...")
        delay(500) // 模拟网络请求挂起
        return "Data_$id"
    }
}
