package com.ysm.techatlas.labs.kotlin.coroutines.functions.scope

import kotlinx.coroutines.*

/**
 * 【函数名称】：supervisorScope (与 coroutineScope 对比)
 * 【作用分类】：作用域函数 (结构化并发的核心)
 *
 * 【日常使用】：
 * 业务场景：一个页面需要并行请求3个接口（头图、列表、推荐），如果推荐接口挂了（抛异常），你**不希望**头图和列表也被取消掉，那就用 `supervisorScope`。
 *
 * 【掌握要点】：
 * - `coroutineScope` (普通作用域)：**“一损俱损”**。只要有一个子协程抛出异常失败，所有其他兄弟协程、父协程全都会被取消。
 * - `supervisorScope` (监督作用域)：**“互不干涉”**。一个子协程失败，不会波及兄弟和父级。异常需要子协程自己内部 try-catch 处理。
 *
 * 【注意事项 & 面试常考点】：
 * - **面试题**：如果在 viewModelScope 中用 launch 启动了多个任务，其中一个失败了，其他会受影响吗？
 *   答：viewModelScope 底层默认使用的是 SupervisorJob，所以它就类似于 SupervisorScope，子任务之间相互独立，不会因为一个失败导致整个 ViewModel 绑定的所有协程被干掉。
 */
object SupervisorScopeLab {
    fun runDemo() = runBlocking {
        println("\n=== [SupervisorScope Lab] ===")
        
        // 我们用监督作用域包裹
        supervisorScope {
            val child1 = launch {
                delay(100)
                println("  [Child-1] 正在执行，准备抛出异常...")
                throw RuntimeException("Child 1 发生致命错误！") // 异常发生
            }

            val child2 = launch {
                delay(300)
                // 因为是 supervisorScope，所以 Child-1 的死不会影响 Child-2
                println("  [Child-2] 不受 Child-1 影响，执行完毕！") 
            }
            
            // 为了防止控制台报大红错影响阅读，我们在外部用 handler 稍微拦截一下（这属于异常处理的高级话题）
            child1.invokeOnCompletion { 
                println("  [Child-1] 已死亡结束。")
            }
        }
        println("  [SupervisorScope] 内部执行结束，父级并未被摧毁。")
    }
}
