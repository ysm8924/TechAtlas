package com.ysm.techatlas.labs.kotlin.syntax

import kotlin.properties.Delegates

/**
 * Kotlin 属性初始化与委托机制 (lateinit vs lazy vs by)
 * 
 * 核心面试题：lateinit 和 lazy 的区别是什么？自定义 by 委托的代价是什么？
 */
object PropertyInitLab {

    @PublishedApi
    internal const val TAG = "PropertyInitLab"

    // =================================================================================
    // 考点 1: lateinit 延迟初始化
    // =================================================================================
    /**
     * [基本用法]：用于暂时无法在构造函数中赋值的 var 变量。
     * [核心原理]：
     *  1. 底层就是一个普通的 Java 变量。
     *  2. 编译器会在每一次 get() 调用前插入一段校验代码：if (field == null) throw UninitializedPropertyAccessException()。
     * [使用场景]：
     *  1. Android 的生命周期组件（如 Activity.onCreate() 中初始化的 View 或 Presenter）。
     *  2. 依赖注入框架（如 Dagger/Hilt 中通过 @Inject 注入的字段）。
     * [面试踩坑点]：
     *  1. 为什么不能修饰基本类型 (Int, Double)？ -> 因为底层判断是否初始化是通过判 null 实现的，Java 基本类型不能为 null。
     *  2. 只能修饰 var，不能修饰 val，且不能有自定义的 getter/setter。
     */
    lateinit var injectData: String
    // lateinit var count: Int // 编译报错：'lateinit' modifier is not allowed on properties of primitive types

    fun testLateinit() {
        println("\n--- Testing lateinit ---")
        // Kotlin 1.2+ 提供了 .isInitialized 反射判断（注意：只能在词法作用域内调用）
        if (!this::injectData.isInitialized) {
            println("[$TAG] injectData is not initialized yet, initializing now...")
            injectData = "Hilt Injected Data"
        }
        println("[$TAG] injectData: $injectData")
    }

    // =================================================================================
    // 考点 2: by lazy 惰性初始化
    // =================================================================================
    /**
     * [基本用法]：用于第一次访问时才计算/创建对象的 val 变量。
     * [核心原理]：
     *  1. 变量实际上被包在了一个 Lazy<T> 对象里。
     *  2. 第一次调用 get() 时，执行传入的 Lambda 表达式，并把结果缓存到 Lazy 内部的 _value 字段。
     *  3. 后续再调用 get()，直接返回缓存的 _value。
     * [使用场景]：
     *  1. 极耗资源的对象的创建（如数据库连接、复杂的对象树）。
     *  2. 可能根本不会被用到的属性，按需加载节省内存。
     * [面试踩坑点]：
     *  1. 默认的 lazy 是线程安全的 (SYNCHRONIZED)，内部使用了 DCL (双重检查锁)。
     *  2. 性能代价：创建 lazy 属性会额外生成一个 Lazy 实例，Lambda 也会生成一个 Function0 实例。如果仅仅是简单对象，不一定要用 lazy。
     */
    val heavyConfig: String by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { // 默认就是 SYNCHRONIZED
        println("[$TAG] --- Doing heavy IO loading for config ---")
        "Config Loaded"
    }

    // UI 线程专用的无锁 lazy（极大提升性能，消除 synchronized 开销）
    val uiConfig: String by lazy(LazyThreadSafetyMode.NONE) {
        "UI Config Loaded"
    }

    fun testLazy() {
        println("\n--- Testing by lazy ---")
        println("[$TAG] Before accessing heavyConfig")
        println("[$TAG] 1st access: $heavyConfig") // 触发加载
        println("[$TAG] 2nd access: $heavyConfig") // 直接读缓存
    }

    // =================================================================================
    // 考点 3: by 委托机制 (Delegation)
    // =================================================================================
    /**
     * [核心原理]：
     * `var prop by Delegate()` 
     * 编译器会自动将 prop 的 get() 和 set() 方法，转发给 Delegate 对象的 getValue() 和 setValue() 方法。
     * 
     * [实际使用场景]：
     * 1. 属性监听：Delegates.observable (值改变时触发回调)
     * 2. 属性拦截：Delegates.vetoable (根据条件决定是否允许修改值)
     * 3. Map 映射：将 JSON 解析的 Map 委托给对象的属性。
     * 4. Compose 中的状态：var text by mutableStateOf("")。
     */
    var observableName: String by Delegates.observable("Initial") { property, oldValue, newValue ->
        println("[$TAG] Property '${property.name}' changed from '$oldValue' to '$newValue'")
    }

    var vetoableAge: Int by Delegates.vetoable(18) { property, oldValue, newValue ->
        println("[$TAG] Trying to change age from $oldValue to $newValue")
        newValue >= 18 // 拦截规则：只有大于等于 18 岁才允许修改成功
    }

    fun testDelegation() {
        println("\n--- Testing Delegation (by) ---")
        observableName = "Alice"
        observableName = "Bob"

        vetoableAge = 20
        println("[$TAG] Current age: $vetoableAge") // 修改成功，打印 20
        vetoableAge = 15 // 被拦截！
        println("[$TAG] Current age after trying to set 15: $vetoableAge") // 依然是 20
    }

    // ================== 测试入口 ==================
    fun runTests() {
        println("========== PropertyInitLab Tests Start ==========")
        testLateinit()
        testLazy()
        testDelegation()
        println("========== PropertyInitLab Tests End ==========\n")
    }
}