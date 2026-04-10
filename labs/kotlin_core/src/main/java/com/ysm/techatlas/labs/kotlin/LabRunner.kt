package com.ysm.techatlas.labs.kotlin

import com.ysm.techatlas.labs.kotlin.inline.InlineLab
import com.ysm.techatlas.labs.kotlin.syntax.SyntaxLab
import com.ysm.techatlas.labs.kotlin.syntax.PropertyInitLab
import com.ysm.techatlas.labs.kotlin.oop.OopLab
import com.ysm.techatlas.labs.kotlin.typesystem.TypeSystemLab
import com.ysm.techatlas.labs.kotlin.initorder.KotlinInitOrderLab
import com.ysm.techatlas.labs.kotlin.classes.DataClassLab
import com.ysm.techatlas.labs.kotlin.classes.ObjectLab
import com.ysm.techatlas.labs.kotlin.classes.SealedClassLab
import com.ysm.techatlas.labs.kotlin.singleton.JavaSingletonLab
import com.ysm.techatlas.labs.kotlin.singleton.KotlinSingletonLab
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

    // 执行单例模式深度对比与实战
    JavaSingletonLab.runTests()
    KotlinSingletonLab.runTests()

    // 执行现代化类系统 (Data Class, Object, Sealed Class) 相关的测试
    // DataClassLab.runTests()
    // ObjectLab.runTests()
    // SealedClassLab.runTests()

    // 执行类加载与初始化顺序相关的测试
    // KotlinInitOrderLab.runTests()

    // 执行高级类型系统测试 (Nothing, in/out, value class)
    // TypeSystemLab.runTests()

    // 执行属性初始化与委托机制测试
    // PropertyInitLab.runTests()

    // 执行协程基础相关的测试
    // CoroutinesBasicLab.runTests()

    // 执行协程底层的 CPS 与状态机观察
    // CpsAndStateMachineLab.runTests()

    // 执行内联机制相关的测试
    // InlineLab.runTests()

    // 执行基础语法与糖相关的测试
    // SyntaxLab.runTests()

    // 执行面向对象：重写与重载相关的测试
    // OopLab.runTests()

    println("\n>>> Kotlin Labs Execution Finished.")
}