package com.ysm.techatlas.labs.kotlin.inline

/**
 * Kotlin Inline 机制实战与验证
 * 
 * 建议操作：使用 Android Studio -> Tools -> Kotlin -> Show Kotlin Bytecode -> Decompile
 * 观察 Java 代码的区别。
 */
object InlineLab {

    // 【修改点】：在 public inline 函数中使用的成员必须是 public 或者是 @PublishedApi 标记的 internal
    // 否则编译报错："Public-API inline function cannot access non-public-API property"
    @PublishedApi
    internal const val TAG = "InlineLab"

    // =================================================================================
    // 考点 1: 普通高阶函数 vs 内联高阶函数 (性能代价差异)
    // =================================================================================

    /**
     * 【非内联】高阶函数
     * 编译代价：调用时会生成一个 Function0 实例。
     */
    fun measureTimeNormal(block: () -> Unit) {
        val start = System.currentTimeMillis()
        block()
        println("[$TAG] Normal Cost: ${System.currentTimeMillis() - start} ms")
    }

    /**
     * 【内联】高阶函数
     * 编译代价：零分配。代码被展开到调用处。
     */
    inline fun measureTimeInline(block: () -> Unit) {
        val start = System.currentTimeMillis()
        block()
        println("[$TAG] Inline Cost: ${System.currentTimeMillis() - start} ms")
    }

    // =================================================================================
    // 考点 2: noinline 的使用场景
    // =================================================================================

    /**
     * 如果我们需要把 block 作为一个对象传递给其他非 inline 函数，或者保存为变量，
     * 必须使用 [noinline] 阻止其被内联。
     */
    inline fun executeAndSave(
        inlineBlock: () -> Unit,
        noinline saveBlock: () -> Unit
    ): Runnable {
        inlineBlock() // 这个会被内联展开
        
        // saveBlock 因为被 noinline 标记，保留了对象形态，可以被传递给匿名内部类/Runnable
        return Runnable { saveBlock() } 
    }

    // =================================================================================
    // 考点 3: crossinline 与非局部返回 (Non-local return)
    // =================================================================================

    /**
     * [crossinline] 用于在 inline 函数中，由于 block 被放在了局部对象或子线程中执行，
     * 必须禁止调用方使用 `return` 直接退出外部函数。
     */
    inline fun executeAsync(crossinline block: () -> Unit) {
        // 如果不加 crossinline，编译器会报错：
        // "Can't inline 'block' here: it may contain non-local returns."
        Thread {
            block()
        }.start()
    }

    // =================================================================================
    // 考点 4: reified 具化泛型
    // =================================================================================

    /**
     * 【常见面试题】：如何在运行时获取泛型的 Class 对象？
     * 答案：借助 inline + reified。由于内联，编译器在调用点知道具体的类型 T。
     */
    inline fun <reified T> printType() {
        // 直接可以拿到 T::class.java，而不需要传入 Class<T> clazz 参数
        println("[$TAG] The type is: ${T::class.java.simpleName}")
    }
    
    // ================== 测试入口 ==================
    fun runTests() {
        println("========== InlineLab Tests Start ==========")
        // 测试内联返回机制
//        testReturn()
        executeAndSave({
            println("========== InlineLab executeAndSave inlineBlock ==========")
        },{
            println("========== InlineLab executeAndSave saveBlock ==========")
        })
        // 测试 reified
//        printType<String>() // 字节码会直接变成 println("... " + String.class.getSimpleName())
//        printType<Int>()
        println("========== InlineLab Tests End ==========\n")
    }

    private fun testReturn() {
        measureTimeInline {
            println("[$TAG] Executing...")
            // 因为是 inline，这里的 return 会直接结束 testReturn 函数！这叫非局部返回。
            return
        }
        println("[$TAG] This line will NEVER be executed.")
    }
}