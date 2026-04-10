package com.ysm.techatlas.labs.kotlin.classes

/**
 * Object (单例 / 伴生对象 / 匿名内部类) 验证室
 */
object ObjectLab {

    @PublishedApi
    internal const val TAG = "ObjectLab"

    // =================================================================================
    // 考点 1: Object 声明 (饿汉式/懒汉式结合的单例)
    // =================================================================================
    /**
     * Kotlin 的 object 编译后，是一个带有 private 构造方法的普通类。
     * 它在静态代码块中（static { INSTANCE = new Singleton() }）进行了初始化。
     * 这依赖于 JVM 类加载机制，因此是【绝对线程安全】的。
     */
    object AppConfig {
        var version: String = "1.0.0"
        fun printConfig() {
            println("[$TAG] Config version is $version")
        }
    }

    // =================================================================================
    // 考点 2: 伴生对象 Companion Object
    // =================================================================================
    class NetworkClient {
        companion object {
            val DEFAULT_TIMEOUT = 5000
            
            @JvmStatic
            fun create(): NetworkClient = NetworkClient()
        }
    }

    // =================================================================================
    // 考点 3: 对象表达式 (匿名内部类)
    // =================================================================================
    // Java 中是 new Runnable() {}，Kotlin 中是 object : Runnable {}
    fun executeTask() {
        // 与 Java 匿名内部类不同的是，Kotlin 的 object 表达式可以继承多个接口/类！
        val myListener = object : Runnable, AutoCloseable {
            override fun run() {
                println("[$TAG] Running task")
            }
            override fun close() {
                println("[$TAG] Closing resources")
            }
        }
        myListener.run()
        myListener.close()
    }

    fun runTests() {
        println("\n--- ObjectLab Tests ---")
        AppConfig.printConfig()
        println("[$TAG] NetworkClient timeout: ${NetworkClient.DEFAULT_TIMEOUT}")
        executeTask()
    }
}