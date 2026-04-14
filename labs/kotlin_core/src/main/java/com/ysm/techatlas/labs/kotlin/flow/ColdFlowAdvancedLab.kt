package com.ysm.techatlas.labs.kotlin.flow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

/**
 * 冷流 (Cold Flow) 进阶核心玩法与实战技巧
 */
object ColdFlowAdvancedLab {

    @PublishedApi
    internal const val TAG = "ColdFlowAdvanced"

    // =================================================================================
    // 技巧 1: 避免 Collect 嵌套地狱 (Flattening 扁平化操作符)
    // 场景：你根据用户的搜索关键词(产生流)，去网络请求搜索结果(又产生流)。
    // =================================================================================
    
    // 模拟一个极其耗时的网络请求，根据输入返回结果流
    private fun requestDataFromNetwork(keyword: String): Flow<String> = flow {
        println("[$TAG] Network request started for: $keyword")
        delay(200) // 模拟网络延迟
        emit("Result for $keyword")
    }

    private fun testFlatMapLatest() = runBlocking {
        println("\n--- Testing flatMapLatest (Search Debounce / Cancellation) ---")
        
        // 模拟用户连续打字输入流
        val userInputFlow = flowOf("K", "Ko", "Kot", "Kotl", "Kotlin")
            .onEach { delay(50) } // 用户每 50ms 敲一个字母

        // 错误做法：在 collect 里面嵌套调用另一个 Flow 的 collect。会导致回调地狱和难以取消。
        /*
        userInputFlow.collect { keyword ->
            requestDataFromNetwork(keyword).collect { result -> ... }
        }
        */

        // 满分做法：使用 flatMapLatest。
        // 如果旧的网络请求还没回来，新的输入又来了，自动 Cancel 掉旧的网络请求！节约极大资源！
        @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
        userInputFlow
            .flatMapLatest { keyword ->
                requestDataFromNetwork(keyword)
            }
            .collect { result ->
                println("[$TAG] UI Update: $result")
            }
        // 最终只会打印出 "Result for Kotlin"，前面的请求全被取消了。
    }

    // =================================================================================
    // 技巧 2: 多数据源合并 (zip vs combine)
    // 场景：页面需要同时请求用户信息和配置信息，两者都回来后才刷新 UI。
    // =================================================================================
    private fun testCombining() = runBlocking {
        println("\n--- Testing combine (Parallel fetching) ---")
        
        val userInfoFlow = flow {
            delay(100)
            emit("User(Lucky)")
        }
        
        val configFlow = flow {
            delay(150)
            emit("Config(Dark Mode)")
        }

        val startTime = System.currentTimeMillis()
        
        // combine 会并发收集两个 Flow，并在任何一个发生改变时，结合它们最新的值进行下发。
        // （这里两者都发 1 次，所以总共执行 1 次。且时间取最长的 150ms，而不是串行的 250ms）
        userInfoFlow.combine(configFlow) { user, config ->
            "Merged -> $user + $config"
        }.collect { combinedResult ->
            val cost = System.currentTimeMillis() - startTime
            println("[$TAG] Combine Result: $combinedResult (Cost: $cost ms)")
        }
    }

    // =================================================================================
    // 技巧 3: 异常恢复与重试策略
    // =================================================================================
    private fun testExceptionAndRetry() = runBlocking {
        println("\n--- Testing catch and retry ---")
        
        var attemptCount = 0
        flow {
            emit("Fetching Data...")
            attemptCount++
            if (attemptCount < 3) {
                println("[$TAG] Attempt $attemptCount failed!")
                throw RuntimeException("Network timeout")
            } else {
                emit("Data Success!")
            }
        }
        // 【技巧】：遇到异常直接重试！重试 2 次 (总共跑 3 次)
        .retry(2) { cause -> 
            cause is RuntimeException 
        }
        // 【防坑】：catch 只能捕获在它上面的操作符抛出的异常，不能捕获它下面 (如 collect 内) 的异常。
        .catch { e -> 
            println("[$TAG] Final Catch: Failed after all retries -> ${e.message}")
            // 异常捕获后，可以发射一个默认值恢复流
            emit("Fallback Default Data") 
        }
        .collect { value ->
            println("[$TAG] UI received: $value")
        }
    }

    // ================== 测试入口 ==================
    fun runTests() {
        println("========== ColdFlowAdvancedLab Tests Start ==========")
        testFlatMapLatest()
        testCombining()
        testExceptionAndRetry()
        println("========== ColdFlowAdvancedLab Tests End ==========\n")
    }
}