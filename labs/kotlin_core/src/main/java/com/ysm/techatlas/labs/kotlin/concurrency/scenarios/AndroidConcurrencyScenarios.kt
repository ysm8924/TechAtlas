package com.ysm.techatlas.labs.kotlin.concurrency.scenarios

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Android 典型多线程场景实验室
 */
object AndroidConcurrencyScenarios {

    private const val TAG = "ConcurrencyScenarios"

    // =================================================================================
    // 场景 1：多线程写缓存 (解决"数据安全"问题)
    // 关键字：锁 (Lock / Mutex)
    // =================================================================================
    private val memoryCache = mutableMapOf<String, String>()
    private val mutex = Mutex()

    /**
     * 模拟：多个后台任务同时写 Log 或更新缓存
     */
    private fun testSharedResourceAccess() = runBlocking {
        println("\n--- Scenario 1: Accessing Shared Map ---")
        val jobs = List(10) { id ->
            launch(Dispatchers.Default) {
                // 如果不加锁，多线程同时对这个 Map 执行 put 会有概率抛出异常或数据丢失
                mutex.withLock {
                    memoryCache["key_$id"] = "value_$id"
                    // println("[$TAG] Thread ${Thread.currentThread().name} wrote data $id")
                }
            }
        }
        jobs.forEach { it.join() }
        println("[$TAG] Cache size: ${memoryCache.size} (Expected 10)")
    }

    // =================================================================================
    // 场景 2：任务间的依赖与等待 (解决"逻辑顺序"问题)
    // 关键字：挂起 (Suspend) / async / await
    // =================================================================================
    /**
     * 模拟启动页逻辑：1.获取 Token -> 2.根据 Token 拉取配置 -> 3.进入主页
     */
    private fun testTaskSequence() = runBlocking {
        println("\n--- Scenario 2: Chaining Tasks (Serial) ---")
        val startTime = System.currentTimeMillis()

        // 步骤 1：获取 Token
        val token = fetchToken()
        
        // 步骤 2：拉取配置 (依赖步骤 1 的结果)
        val config = fetchConfig(token)
        
        println("[$TAG] All tasks finished. Config: $config (Cost: ${System.currentTimeMillis() - startTime}ms)")
    }

    private suspend fun fetchToken(): String {
        delay(200) // 模拟耗时
        return "TOKEN_12345"
    }

    private suspend fun fetchConfig(token: String): String {
        delay(300)
        return "Config_for_$token"
    }

    // =================================================================================
    // 场景 3：多个异步任务并发合并 (解决"效率"问题)
    // 关键字：并行 (Parallelism)
    // =================================================================================
    /**
     * 模拟：主页展示需要同时从 广告接口 和 推荐接口 拿数据
     */
    private fun testParallelTasks() = runBlocking {
        println("\n--- Scenario 3: Merging Tasks (Parallel) ---")
        val startTime = System.currentTimeMillis()

        // 两个任务并发开启，互不等待
        val adDeferred = async { fetchAdData() }
        val newsDeferred = async { fetchNewsData() }

        // 等待它们都回来
        val result = "Result: ${adDeferred.await()} & ${newsDeferred.await()}"
        
        // 总耗时应该是两个任务中最长的那一个 (300ms)，而不是相加 (500ms)
        println("[$TAG] Combined: $result (Total Cost: ${System.currentTimeMillis() - startTime}ms)")
    }

    private suspend fun fetchAdData(): String {
        delay(200)
        return "Ad_Data"
    }

    private suspend fun fetchNewsData(): String {
        delay(300)
        return "News_Data"
    }

    fun runTests() {
        println("========== Android Concurrency Scenarios Start ==========")
        testSharedResourceAccess()
        testTaskSequence()
        testParallelTasks()
        println("========== Android Concurrency Scenarios End ==========\n")
    }
}
