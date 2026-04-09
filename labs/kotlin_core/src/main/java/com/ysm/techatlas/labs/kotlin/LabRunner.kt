package com.ysm.techatlas.labs.kotlin

import com.ysm.techatlas.labs.kotlin.inline.InlineLab
import com.ysm.techatlas.labs.kotlin.syntax.SyntaxLab
import com.ysm.techatlas.labs.kotlin.oop.OopLab
import com.ysm.techatlas.labs.kotlin.coroutines.CoroutinesBasicLab
import com.ysm.techatlas.labs.kotlin.coroutines.CpsAndStateMachineLab

/**
 * 纯 Kotlin JVM 运行入口
 * 
 * 操作说明：
 * 1. 直接点击左侧的绿色 "Play" 按钮。
 * 2. 选择 "Run 'LabRunnerKt'"。
 * 3. 这种运行方式不需要编译 Android APK，也不需要启动模拟器，直接在本地 JVM 极速执行并输出结果。
 */
fun main() {
    println(">>> Starting Kotlin Labs Execution...\n")

    // 执行协程基础相关的测试
    // CoroutinesBasicLab.runTests()

    // 执行协程底层的 CPS 与状态机观察
    CpsAndStateMachineLab.runTests()

    // 执行内联机制相关的测试
    // InlineLab.runTests()

    // 执行基础语法与糖相关的测试
    // SyntaxLab.runTests()

    // 执行面向对象：重写与重载相关的测试
    // OopLab.runTests()

    println(">>> Kotlin Labs Execution Finished.")
}