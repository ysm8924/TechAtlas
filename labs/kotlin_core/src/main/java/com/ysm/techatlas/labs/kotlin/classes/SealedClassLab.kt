package com.ysm.techatlas.labs.kotlin.classes

/**
 * Sealed Class (密封类) 验证室
 */
object SealedClassLab {

    @PublishedApi
    internal const val TAG = "SealedClassLab"

    // =================================================================================
    // 考点 1: Sealed Class 的定义与状态穷举
    // 常用于 MVI 架构中的 UI 状态管理
    // =================================================================================
    sealed class UiState {
        // 无状态的子类可以用 object (单例节省内存)
        object Loading : UiState()
        // 需要携带数据的子类用 data class
        data class Success(val data: List<String>) : UiState()
        data class Error(val exception: Throwable) : UiState()
    }

    // =================================================================================
    // 考点 2: 编译器穷举检查 (Exhaustive Check)
    // =================================================================================
    fun render(state: UiState) {
        // 【核心优势】：这里不需要写 else 分支。
        // 如果你偷偷给 UiState 加了一个新的子类 Empty，这里的 when 会直接编译报错！
        // 这强迫开发者必须处理所有可能的业务状态，消除了运行时的未知状态 Bug。
        val message = when (state) {
            is UiState.Loading -> "Show Progress Bar"
            is UiState.Success -> "Show List with ${state.data.size} items"
            is UiState.Error -> "Show Toast: ${state.exception.message}"
        }
        println("[$TAG] Render result: $message")
    }

    // =================================================================================
    // 考点 3: Sealed Interface (密封接口)
    // =================================================================================
    /**
     * Kotlin 1.5 引入。
     * 当你需要让一个类实现多个密封体系，或者不希望占用类的单继承名额时使用。
     */
    sealed interface ErrorCode
    enum class HttpError : ErrorCode {
        NOT_FOUND, SERVER_ERROR
    }
    enum class LocalError : ErrorCode {
        DB_CORRUPT, IO_EXCEPTION
    }

    fun handleError(error: ErrorCode) {
        when(error) {
            is HttpError -> println("[$TAG] Handling HTTP Error: $error")
            is LocalError -> println("[$TAG] Handling Local Error: $error")
        }
    }

    fun runTests() {
        println("\n--- SealedClassLab Tests ---")
        val state1 = UiState.Loading
        val state2 = UiState.Success(listOf("Item 1", "Item 2"))
        
        render(state1)
        render(state2)
        handleError(HttpError.NOT_FOUND)
    }
}