package com.ysm.techatlas.labs.kotlin.flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * 热流 (Hot Flow: StateFlow & SharedFlow) 验证室
 * (取代 LiveData 和 EventBus 的现代利器)
 */
object HotFlowLab {

    @PublishedApi
    internal const val TAG = "HotFlowLab"

    // =================================================================================
    // 考点 1: StateFlow (UI 状态持有者，完美替代 LiveData)
    // 特性：必须有初始值、永远重播最新状态、连续相同的值被过滤防抖 (Conflated)
    // =================================================================================
    private val _uiState = MutableStateFlow("Loading")
    val uiState: StateFlow<String> = _uiState

    private fun testStateFlow() = runBlocking {
        println("\n--- Testing StateFlow ---")
        
        // 开启一个协程去收集
        val job = launch {
            uiState.collect { state ->
                println("[$TAG] Collector 1 received state: $state")
            }
        }

        delay(50)
        _uiState.value = "Success"
        
        // 【防抖测试】：连续发送相同的 "Success"，Collector 只会收到一次！
        delay(50)
        _uiState.value = "Success"
        
        // 【粘性测试 (重播最新状态)】：新的订阅者进来，立刻能收到当前最新的状态
        delay(50)
        val job2 = launch {
            uiState.collect { state ->
                println("[$TAG] Collector 2 (Late) received state: $state")
            }
        }
        
        delay(100)
        job.cancel()
        job2.cancel()
    }

    // =================================================================================
    // 考点 2: SharedFlow (一次性事件总线，完美替代 EventBus)
    // 特性：无需初始值、默认不防抖、可配置重播 (replay) 策略
    // =================================================================================
    // replay = 0: 新订阅者收不到以前发过的事件
    // extraBufferCapacity = 1: 缓冲1个事件防止生产者挂起
    private val _eventFlow = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    val eventFlow: SharedFlow<String> = _eventFlow

    private fun testSharedFlow() = runBlocking {
        println("\n--- Testing SharedFlow ---")
        
        val job = launch {
            eventFlow.collect { event ->
                println("[$TAG] Collector 1 received event: $event")
            }
        }

        delay(50)
        // 发送事件用 emit (由于在非挂起函数里一般用 tryEmit)
        _eventFlow.tryEmit("Toast: Network Error")
        
        // 【不防抖测试】：连续发送相同事件，Collector 都会收到！
        delay(50)
        _eventFlow.tryEmit("Toast: Network Error")

        delay(50)
        // 【无粘性测试】：新的订阅者进来，因为 replay=0，它收不到前面的 Toast！
        val job2 = launch {
            eventFlow.collect { event ->
                println("[$TAG] Collector 2 (Late) received event: $event")
            }
        }
        
        delay(50)
        _eventFlow.tryEmit("Toast: DB Error") // 此时 Collector 1 和 2 都能收到

        delay(100)
        job.cancel()
        job2.cancel()
    }

    // ================== 测试入口 ==================
    fun runTests() {
        println("========== HotFlowLab Tests Start ==========")
        testStateFlow()
        testSharedFlow()
        println("========== HotFlowLab Tests End ==========\n")
    }
}