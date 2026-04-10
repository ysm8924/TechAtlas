package com.ysm.techatlas.labs.kotlin.singleton

/**
 * Kotlin 版本的核心单例模式实现与 Android 场景考点
 */
object KotlinSingletonLab {

    @PublishedApi
    internal const val TAG = "KotlinSingleton"

    // =================================================================================
    // 1. 饿汉式 -> Object 声明
    // 等价于 Java 的 public static final INSTANCE = new ...
    // =================================================================================
    object SimpleConfig {
        var version = "1.0"
        fun init() {
            println("[$TAG] SimpleConfig init")
        }
    }

    // =================================================================================
    // 2. 无参懒加载 -> by lazy
    // Kotlin 原生支持的语法糖。默认的 LazyThreadSafetyMode.SYNCHRONIZED 就是完美的 DCL。
    // =================================================================================
    class LazyDatabase private constructor() {
        companion object {
            val instance: LazyDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
                println("[$TAG] Creating LazyDatabase instance...")
                LazyDatabase()
            }
        }
    }

    // =================================================================================
    // 3. 有参懒加载 -> Companion Object DCL [Android 必考]
    // 场景：在 Android 中，Room 数据库等组件必须传入 Context 才能初始化。
    // 因为 object 和 by lazy 都不支持传参，此时必须手动实现 DCL！
    // =================================================================================
    class RoomDatabase private constructor(context: String) {
        
        init {
            println("[$TAG] RoomDatabase created with Context: $context")
        }

        companion object {
            // 【核心考点】：@Volatile 映射为 Java 的 volatile，防止指令重排
            @Volatile
            private var instance: RoomDatabase? = null

            // 这里传入 Context (这里用 String 模拟)
            fun getInstance(context: String): RoomDatabase {
                return instance ?: synchronized(this) {
                    instance ?: RoomDatabase(context).also { instance = it }
                }
            }
        }
    }

    fun runTests() {
        println("--- Kotlin Singleton Tests ---")
        SimpleConfig.init()
        
        println("[$TAG] Lazy DB Hash 1: ${LazyDatabase.instance.hashCode()}")
        println("[$TAG] Lazy DB Hash 2: ${LazyDatabase.instance.hashCode()}") // 不会再打印 creating...

        val db1 = RoomDatabase.getInstance("ApplicationContext")
        val db2 = RoomDatabase.getInstance("ActivityContext (Ignored)")
        println("[$TAG] Room DB Hash: ${db1.hashCode()} == ${db2.hashCode()}")
    }
}