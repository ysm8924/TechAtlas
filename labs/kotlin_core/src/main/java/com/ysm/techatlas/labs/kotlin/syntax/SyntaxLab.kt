package com.ysm.techatlas.labs.kotlin.syntax

// =================================================================================
// 考点 1 补充: 顶层扩展函数 (Top-level Extension)
// 这才是真正会被编译为 Java `static` 方法的扩展函数。
// 反编译结果： public static final void printNameTopLevel(Animal $this$printNameTopLevel)
// =================================================================================
open class Animal
class Dog : Animal()

fun Animal.printNameTopLevel() = println("[SyntaxLab] TopLevel: I am Animal")
fun Dog.printNameTopLevel() = println("[SyntaxLab] TopLevel: I am Dog")


/**
 * 语法糖底层代价与面试陷阱验证室
 */
object SyntaxLab {

    private const val TAG = "SyntaxLab"

    // =================================================================================
    // 考点 1: 成员扩展函数 (Member Extension)
    // 因为定义在 object 内部，它变成了 SyntaxLab 单例的实例方法
    // =================================================================================
    private fun Animal.printNameMember() = println("[$TAG] Member: I am Animal")
    private fun Dog.printNameMember() = println("[$TAG] Member: I am Dog")

    fun testExtensionDispatch() {
        val myDog: Animal = Dog()
        
        // 场景 A: 调用顶层扩展函数
        // 字节码: SyntaxLabKt.printNameTopLevel(myDog); -> 真正的静态调用
        myDog.printNameTopLevel() 
        
        // 场景 B: 调用成员扩展函数
        // 字节码: this.printNameMember(myDog); -> SyntaxLab 实例的普通方法调用
        myDog.printNameMember()
        
        // 【核心结论不变】：无论是哪种场景，最终传入和决议的类型，都是声明时的 Animal，而不是 Dog！
    }


    // =================================================================================
    // 考点 2: 伴生对象的开销与 @JvmStatic 优化
    // =================================================================================
    class ConfigManager {
        companion object {
            // 没有优化的伴生属性
            // 底层：生成了 private static String NORMAL_CONFIG，并提供 getNORMAL_CONFIG() 供 Companion 实例调用
            val NORMAL_CONFIG = "NORMAL"

            // 优化 1：编译期常量（仅限基本类型和 String）
            // 底层：直接内联到调用处，没有任何对象分配和方法派发
            const val OPTIMIZED_CONST = "CONST"

            // 优化 2：真正的静态方法
            // 底层：生成了真正的 public static void printConfig()
            @JvmStatic
            fun printConfig() {
                println("[$TAG] Config Printed")
            }

            fun eat(){ println("[$TAG] Eat") }
        }
    }

    // =================================================================================
    // 考点 3: 解构声明的顺序陷阱
    // =================================================================================
    // 假设这是 v1 版本的实体类
    data class UserV1(val name: String, val role: String)
    
    // 假设这是 v2 版本，有人在中间加了一个字段
    data class UserV2(val name: String, val gender: String, val role: String)

    fun testDestructuring() {


        val user1 = UserV1("Alice", "Admin")
        val (n1, r1) = user1
        println("[$TAG] User1: Name=$n1, Role=$r1") // 正常

        // 陷阱复现
        val user2 = UserV2("Bob", "Male", "Admin")
        val (n2, r2) = user2 
        // 【危险】开发者本意是取 Name 和 Role，但因为解构按 component1() 和 component2() 顺序取值，
        // 导致 r2 拿到了 "Male" (gender) 而不是 "Admin"。类型相同，编译不报错，酿成线上 Bug！
        println("[$TAG] User2 (Bug): Name=$n2, Role=$r2") 
    }

    // =================================================================================
    // 考点 4: Lazy 委托的线程安全与性能损耗
    // =================================================================================
    
    // 默认的 lazy，底层自带 Synchronized 锁，确保多线程安全。如果仅在主线程使用，有性能浪费。
    private val defaultLazyValue by lazy {
        println("[$TAG] Init Default Lazy")
        "Value1"
    }

    // 优化：指明不需要线程安全（单线程环境极佳）
    private val fastLazyValue by lazy(LazyThreadSafetyMode.NONE) {
        println("[$TAG] Init Fast Lazy")
        "Value2"
    }

    // =================================================================================
    // 考点 5: 默认参数与方法重载爆炸
    // =================================================================================
    /**
     * Kotlin 中定义默认参数，底层会生成一个带有 mask（标志位）的 synthetic 方法 (createUser$default)
     * 来处理缺省逻辑，避免了生成 N 个重载方法。
     * 但如果 Java 代码要调用，Java 看不到默认参数，必须传全参数。
     * 除非加上 @JvmOverloads，此时编译器才会真的生成一系列重载的 Java 方法。
     */
    @JvmOverloads
    fun createUser(name: String, age: Int = 18, isVip: Boolean = false) {
        println("[$TAG] Create: $name, $age, $isVip")
    }

    fun createUser2(name: String, age: Int = 18, isVip: Boolean = false) {
        println("[$TAG] Create: $name, $age, $isVip")
    }

    fun runTests() {
        println("========== SyntaxLab Tests Start ==========")
        testExtensionDispatch()
        testDestructuring()
        println("[$TAG] $defaultLazyValue")
        println("[$TAG] $fastLazyValue")
        println("========== SyntaxLab Tests End ==========\n")
    }
}