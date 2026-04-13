package com.ysm.techatlas.labs.kotlin.coroutines.functions.builders

import kotlinx.coroutines.*
import kotlin.coroutines.*

/**
 * 【函数名称】：suspendCoroutine / suspendCancellableCoroutine
 * 【作用分类】：协程桥接器 (回调转协程的神器)
 *
 * 【日常使用】：
 * 开发中经常会遇到一些只支持 Callbacks 的老旧 SDK（如定位SDK：onSuccess, onFail）。
 * 你可以通过这两个函数，把回调形式的地狱代码，包装成优雅的、同步返回的挂起函数 (suspend function)。
 *
 * 【掌握要点】：
 * 1. 它们会立刻**挂起**当前协程。
 * 2. 它们暴露了 `Continuation` 参数，你可以把这个参数传到老式接口的 Callback 里。
 * 3. 在 Callback 被触发时，调用 `continuation.resume(value)` 恢复协程并返回值，或 `continuation.resumeWithException(e)` 抛异常。
 * 
 * 【注意事项 & 面试常考点】：
 * - **极其重要**：`suspendCoroutine` 是不支持取消的；而 `suspendCancellableCoroutine` 支持取消（当所在协程被 cancel 时，你可以通过 `continuation.invokeOnCancellation` 做资源清理）。
 * - **规范**：在 Android 开发中，**永远优先使用 `suspendCancellableCoroutine`**，以防内存泄漏。
 */
object SuspendCoroutineLab {
    fun runDemo() = runBlocking {
        println("\n=== [SuspendCoroutine Lab] ===")
        
        println("  [Main] 开始请求定位 (回调转协程)...")
        try {
            val location = fetchLocationSuspend()
            println("  [Main] 拿到定位结果: $location")
        } catch (e: Exception) {
            println("  [Main] 定位失败: ${e.message}")
        }
    }

    /**
     * 将传统的回调接口，封装为符合协程风格的 suspend 方法
     */
    private suspend fun fetchLocationSuspend(): String = suspendCancellableCoroutine { continuation ->
        // 模拟调用第三方老旧的回调接口
        LegacyLocationSDK.requestLocation(object : LegacyLocationSDK.Callback {
            override fun onSuccess(location: String) {
                // 收到成功回调时，恢复协程并返回数据
                if (continuation.isActive) {
                    continuation.resume(location)
                }
            }

            override fun onError(error: String) {
                // 收到失败回调时，恢复协程并抛出异常
                if (continuation.isActive) {
                    continuation.resumeWithException(RuntimeException(error))
                }
            }
        })
        
        // 当协程被取消时（例如页面关闭），通知底层的 SDK 停止定位，防止内存泄漏！
        continuation.invokeOnCancellation {
            LegacyLocationSDK.stopRequest()
            println("  [SDK] 定位请求被外部取消。")
        }
    }

    // --- 模拟的一个老旧的 Callback 风格 SDK ---
    object LegacyLocationSDK {
        interface Callback {
            fun onSuccess(location: String)
            fun onError(error: String)
        }

        fun requestLocation(callback: Callback) {
            println("  [SDK] 开始在后台获取定位...")
            // 模拟异步耗时回调
            Thread {
                Thread.sleep(500)
                callback.onSuccess("Lat: 39.9, Lng: 116.4")
            }.start()
        }
        
        fun stopRequest() { /* 停止硬件传感器等 */ }
    }
}
