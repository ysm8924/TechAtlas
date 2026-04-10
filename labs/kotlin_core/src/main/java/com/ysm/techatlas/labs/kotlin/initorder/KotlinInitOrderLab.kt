package com.ysm.techatlas.labs.kotlin.initorder

/**
 * 核心考点：Kotlin 的加载与初始化顺序实战验证
 */
object KotlinInitOrderLab {

    // 完美复刻你的测试代码
    class Person() {

        /* 第 1 步：实例属性初始化 */
        private var gender: Boolean = true

        /* 第 5 步：次构造函数体（最后执行） */
        constructor(name: String, gender: Boolean) : this() {
            println("[KotlinInitOrder] constructor body")
        }

        // ================== 伴生对象区域 (类加载时按从上到下执行) ==================
        companion object {
            // [极其关键的一步]：在伴生对象的开头，直接触发了类的实例化！
            // 此时伴生对象下面的 init 块会被强制挂起，先去执行 Person 实例的初始化。
            val instance = Person("yzq", false)

            /* 伴生对象中的初始化代码 */
            init {
                println("[KotlinInitOrder] companion init 1")
            }

            init {
                println("[KotlinInitOrder] companion init 2")
            }
        }

        // ================== 实例代码块区域 (与属性按从上到下顺序执行) ==================
        /* 第 2 步：实例初始化代码块 */
        init {
            // 此时 gender 属性已经被赋值了
            println("[KotlinInitOrder] Person init 2, gender:${gender}")
        }

        /* 第 3 步：实例初始化代码块 */
        init {
            println("[KotlinInitOrder] Person init 1")
        }
    }

    fun runTests() {
        println("========== InitOrderLab Tests Start ==========")
        println(">>> Triggering Kotlin Person Initialization...")
        // 只要访问伴生对象，就会触发类加载机制
        val testInstance = Person.instance
        
        // 跑一下对比用的 Java 版本
        JavaInitOrder.runTests()
        println("========== InitOrderLab Tests End ==========\n")
    }
}