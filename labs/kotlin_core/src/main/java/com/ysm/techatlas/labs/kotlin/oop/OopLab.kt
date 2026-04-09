package com.ysm.techatlas.labs.kotlin.oop

/**
 * 面向对象：重写(Override)与重载(Overload)的验证室
 */
object OopLab {

    private const val TAG = "OopLab"

    // =================================================================================
    // 考点 1: 重写(Override) 的动态决议 vs 重载(Overload) 的静态决议
    // =================================================================================
    open class Animal
    class Dog : Animal()

    open class Dispatcher {
        // 这是重载 (Overload)：同名，参数不同
        open fun dispatch(a: Animal) {
            println("[$TAG] Dispatcher receives an Animal")
        }
        
        open fun dispatch(d: Dog) {
            println("[$TAG] Dispatcher receives a Dog")
        }
    }

    class SubDispatcher : Dispatcher() {
        // 这是重写 (Override)：覆盖父类的行为
        override fun dispatch(a: Animal) {
            println("[$TAG] SubDispatcher receives an Animal")
        }

        override fun dispatch(d: Dog) {
            println("[$TAG] SubDispatcher receives a Dog")
        }
    }

    fun testDispatch() {
        println("--- Testing Static vs Dynamic Dispatch ---")
        // 1. 对象的动态决议 (Dynamic Dispatch)
        val dispatcher: Dispatcher = SubDispatcher()
        
        // 2. 参数的静态决议 (Static Dispatch)
        val myDog: Animal = Dog()
        
        // 【面试终极题】：这里会打印什么？
        // 步骤一：决定由谁执行？(看 receiver) 
        // dispatcher 声明是 Dispatcher，实际是 SubDispatcher -> 运行时动态决议 -> 调用 SubDispatcher 的方法。
        // 步骤二：决定调用哪个重载版本？(看 parameter)
        // myDog 声明是 Animal，实际是 Dog -> 编译期静态决议 -> 绑定参数类型为 Animal 的那个方法！
        // 【答案】：SubDispatcher receives an Animal
        dispatcher.dispatch(myDog)
    }

    // =================================================================================
    // 考点 2: Kotlin 独有 - 泛型擦除与 @JvmName 拯救重载
    // =================================================================================
    
    // fun process(list: List<String>) { }
    // fun process(list: List<Int>) { } 
    // 上面两行如果同时存在，会报 "Platform declaration clash"，因为编译后都会变成 process(List list)

    // Kotlin 魔法：通过 @JvmName 改变生成的 Java 字节码的方法名，但在 Kotlin 中调用时名字依然一样。
    fun process(list: List<String>) {
        println("[$TAG] Process String List")
    }

    @JvmName("processIntList")
    fun process(list: List<Int>) {
        println("[$TAG] Process Int List")
    }

    fun testJvmName() {
        println("--- Testing @JvmName Overload ---")
        val strList = listOf("A", "B")
        val intList = listOf(1, 2)
        
        // 在 Kotlin 里调用，感觉像是完美的重载
        process(strList)
        process(intList)
        // 去 Decompile 看看，底层其实是：
        // OopLab.process(strList);
        // OopLab.processIntList(intList); 
    }

    // =================================================================================
    // 考点 3: Kotlin 属性的重写 (Property Override)
    // =================================================================================
    open class BaseClass {
        // open val 只会生成 getter
        open val type: String = "Base"
    }

    class DerivedClass : BaseClass() {
        // 重写为 var，会生成 getter 和 setter
        // 现象：子类扩展了父类的能力，这是合法的。
        override var type: String = "Derived"
    }

    fun testPropertyOverride() {
        println("--- Testing Property Override ---")
        val obj: BaseClass = DerivedClass()
        println("[$TAG] Property value: ${obj.type}") // 输出 Derived
        
        // obj.type = "New" // 编译报错：因为声明类型是 BaseClass，编译器认为它是个 val，没有 setter
        
        if (obj is DerivedClass) {
            obj.type = "New Type" // 智能转换后，可以使用 setter
            println("[$TAG] Property value mutated: ${obj.type}")
        }
    }

    // ================== 测试入口 ==================
    fun runTests() {
        println("========== OopLab Tests Start ==========")
        testDispatch()
        testJvmName()
        testPropertyOverride()
        println("========== OopLab Tests End ==========\n")
    }
}