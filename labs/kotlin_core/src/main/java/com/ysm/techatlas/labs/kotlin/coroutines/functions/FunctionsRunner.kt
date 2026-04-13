package com.ysm.techatlas.labs.kotlin.coroutines.functions

import com.ysm.techatlas.labs.kotlin.coroutines.functions.builders.AsyncLab
import com.ysm.techatlas.labs.kotlin.coroutines.functions.builders.LaunchLab
import com.ysm.techatlas.labs.kotlin.coroutines.functions.builders.RunBlockingLab
import com.ysm.techatlas.labs.kotlin.coroutines.functions.builders.SuspendCoroutineLab
import com.ysm.techatlas.labs.kotlin.coroutines.functions.control.RepeatLab
import com.ysm.techatlas.labs.kotlin.coroutines.functions.lifecycle.DelayLab
import com.ysm.techatlas.labs.kotlin.coroutines.functions.lifecycle.EnsureActiveLab
import com.ysm.techatlas.labs.kotlin.coroutines.functions.lifecycle.NonCancellableLab
import com.ysm.techatlas.labs.kotlin.coroutines.functions.lifecycle.WithTimeoutLab
import com.ysm.techatlas.labs.kotlin.coroutines.functions.lifecycle.YieldLab
import com.ysm.techatlas.labs.kotlin.coroutines.functions.scope.CoroutineScopeLab
import com.ysm.techatlas.labs.kotlin.coroutines.functions.scope.SupervisorScopeLab
import com.ysm.techatlas.labs.kotlin.coroutines.functions.scope.WithContextLab

/**
 * 协程高阶函数学习章节 集中运行入口
 */
object FunctionsRunner {
    fun runAll() {
        println("========= 开始执行协程高阶函数解析 =========\n")
        
        // --- 启动与桥接篇 ---
        RunBlockingLab.runDemo()
        LaunchLab.runDemo()
        AsyncLab.runDemo()
        SuspendCoroutineLab.runDemo()

        // --- 作用域与上下文篇 ---
        WithContextLab.runDemo()
        CoroutineScopeLab.runDemo()
        SupervisorScopeLab.runDemo()

        // --- 生命周期与取消机制篇 ---
        DelayLab.runDemo()
        YieldLab.runDemo()
        EnsureActiveLab.runDemo()
        NonCancellableLab.runDemo()
        WithTimeoutLab.runDemo()

        // --- 流程控制篇 ---
        RepeatLab.runDemo()

        println("\n========= 协程高阶函数解析 执行完毕 =========")
    }
}
