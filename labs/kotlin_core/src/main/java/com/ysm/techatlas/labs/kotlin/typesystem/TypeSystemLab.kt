package com.ysm.techatlas.labs.kotlin.typesystem

/**
 * Kotlin 高级类型系统实战验证室
 * (Any, Unit, Nothing, 泛型 out/in, value class, Type Projection, 星号投影)
 */
object TypeSystemLab {

    @PublishedApi
    internal const val TAG = "TypeSystemLab"

    // =================================================================================
    // 考点 1: Nothing 底层类型 (Bottom Type)
    // =================================================================================
    /**
     * 抛出异常的函数返回类型是 Nothing。
     * 因为 Nothing 是所有类的子类，它可以在任何需要类型推断的地方充当万能占位符。
     */
    private fun fail(message: String): Nothing {
        throw IllegalArgumentException(message)
    }

    fun testNothing() {
        println("\n--- Testing Nothing ---")
        val name: String? = "Lucky"
        // 这里的 ?: 右侧调用了 fail()。
        // 如果 fail() 返回 void，这里编译会报错，因为 String 无法与 void 调和。
        // 但因为 fail() 返回 Nothing，Nothing 是 String 的子类，所以 realName 被完美推断为 String!
        val realName: String = name ?: fail("Name cannot be null")
        println("[$TAG] Real name is $realName")
    }

    // =================================================================================
    // 考点 2: 泛型型变 - out (协变) 与 in (逆变) [声明处型变]
    // =================================================================================
    open class Animal
    class Dog : Animal()

    // 【1. 协变 out】: Producer (只读不写)
    // 加上 out 后，Producer<Dog> 变成了 Producer<Animal> 的子类！
    class Producer<out T>(private val item: T) {
        fun produce(): T = item
        // fun consume(item: T) {} // 编译报错：Type parameter T is declared as 'out' but occurs in 'in' position
    }

    // 【2. 逆变 in】: Consumer (只写不读)
    // 加上 in 后，Consumer<Animal> 反而变成了 Consumer<Dog> 的子类！(逻辑反转)
    class Consumer<in T> {
        fun consume(item: T) {
            println("[$TAG] Consumed item: ${item?.javaClass?.simpleName}")
        }
        // fun produce(): T // 编译报错：Type parameter T is declared as 'in' but occurs in 'out' position
    }

    fun testVariance() {
        println("\n--- Testing Variance (out / in) ---")
        
        // 协变测试 (Dog 也是 Animal)
        val dogProducer: Producer<Dog> = Producer(Dog())
        val animalProducer: Producer<Animal> = dogProducer // 因为 out，这是合法的！
        println("[$TAG] Produced: ${animalProducer.produce().javaClass.simpleName}")

        // 逆变测试 (反向继承)
        val animalConsumer: Consumer<Animal> = Consumer<Animal>()
        // 要求传入 Consumer<Dog> 的地方，我传 Consumer<Animal> 是完全安全的！
        val dogConsumer: Consumer<Dog> = animalConsumer 
        dogConsumer.consume(Dog())
    }

    // =================================================================================
    // 考点 3: value class (内联值类) - 零成本抽象
    // =================================================================================
    /**
     * 包装了 String，但在运行时完全不存在 Password 这个对象！
     * 编译后，所有 Password 都会被擦除还原成 String。
     * 既保证了类型安全（防止把普通 String 当密码传错），又消除了对象分配开销。
     */
    @JvmInline
    value class Password(val value: String) {
        // 可以定义方法，这些方法编译后会变成静态方法，接收底层类型 String 作为参数
        fun isValid(): Boolean = value.length >= 6
    }

    fun authenticate(pwd: Password) {
        println("[$TAG] Authenticating: is valid? ${pwd.isValid()}")
    }

    fun testValueClass() {
        println("\n--- Testing value class ---")
        // 如果 Decompile Java，这一行本质上就是 String securePwd = "mySecretPassword";
        val securePwd = Password("mySecretPassword")
        
        // authenticate("12345") // 编译报错！无法把 String 混入 Password，类型极其安全
        authenticate(securePwd)
    }

    // =================================================================================
    // 补充考点 4: 星号投影 (Star Projection) -> `*`
    // =================================================================================
    /**
     * 当你不知道泛型参数的具体类型，或者不关心具体类型时，可以使用 `*`。
     * 它类似于 Java 的 `?` 通配符。
     * - 对于 Foo<out T>，`Foo<*>` 等价于 `Foo<Any?>` （安全地读）
     * - 对于 Foo<in T>，`Foo<*>` 等价于 `Foo<Nothing>` （写被彻底禁止，非常安全）
     * - 对于 Foo<T>，读时当 Any?，写时禁止。
     */
    fun printListLength(list: List<*>) {
        // list.add(1) // 编译报错：禁止写
        println("[$TAG] List length is ${list.size}")
        @Suppress("UNUSED_VARIABLE")
        val item: Any? = list.firstOrNull() // 读出来只能是 Any?
    }

    fun testStarProjection() {
        println("\n--- Testing Star Projection ---")
        val ints = listOf(1, 2, 3)
        val strings = listOf("A", "B")
        printListLength(ints)
        printListLength(strings)
    }

    // =================================================================================
    // 补充考点 5: 类型投影 (Type Projection) - 解决使用处的型变
    // =================================================================================
    /**
     * Array 在 Kotlin 中是不型变的（Invariant）。
     * 即 Array<String> 不是 Array<Any> 的子类。
     * 如果我们需要写一个能拷贝任何数组的函数，必须在【使用处】加上 out 限制。
     */
    fun copyArray(from: Array<out Any>, to: Array<Any>) {
        // 在这里，from 被限制为了“协变”，编译器保证你不能调用 from.set()，只能 get()
        for (i in from.indices) {
            to[i] = from[i]
        }
    }

    fun testTypeProjection() {
        println("\n--- Testing Type Projection ---")
        val ints: Array<Int> = arrayOf(1, 2, 3)
        val anyArray: Array<Any> = Array(3) { "" }
        
        // 如果上面 copyArray 方法第一个参数没写 `out`，这里编译会报错。
        // 因为 Array<Int> 不是 Array<Any>。
        // 加了 `out Any` 后，它就相当于 Java 的 Array<? extends Object>。
        copyArray(ints, anyArray)
        println("[$TAG] Copied array first item: ${anyArray[0]}")
    }

    // ================== 测试入口 ==================
    fun runTests() {
        println("========== TypeSystemLab Tests Start ==========")
        try { testNothing() } catch (e: Exception) { println(e.message) }
        testVariance()
        testValueClass()
        testStarProjection()
        testTypeProjection()
        println("========== TypeSystemLab Tests End ==========\n")
    }
}