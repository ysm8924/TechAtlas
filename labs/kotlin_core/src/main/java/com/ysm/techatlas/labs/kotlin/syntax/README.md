# Kotlin 基础语法与语法糖深度剖析

Kotlin 提供了极具表现力的语法糖，但在享受便捷的同时，如果不了解其在 JVM 底层的映射原理，极易在不知不觉中写出低效或行为诡异的代码。这是各大厂面试的高频考区。

## 1. 扩展函数 (Extension Functions) 的本质：静态决议
*   **现象**：仿佛可以给任何类（如 `String`）“动态”添加新方法。
*   **底层代价/原理**：扩展函数在字节码层面的映射，取决于它定义的位置：
    *   **顶层扩展 (Top-level)**：定义在类/对象外部。在 JVM 中会被编译为 `<FileName>Kt.class` 中的 **`public static` 静态方法**。这是最常见的形态。
    *   **成员扩展 (Member)**：定义在某个 Class 或 Object 内部（如我们代码中的 `SyntaxLab` 内）。在 JVM 中会被编译为该宿主类的**实例方法**（如 `private final`）。
*   **面试核心考点（静态绑定）**：无论是编译成真实的 `static` 还是实例方法，扩展函数的“多态”决议都是**静态的（Static Dispatch）**！如果父类和子类有同名的扩展函数，调用哪个完全取决于**编译期声明的变量类型**，而不是运行时的实际对象类型。

## 2. 伴生对象 (Companion Object) 的性能陷阱
*   **现象**：Kotlin 取消了 `static` 关键字，推荐使用伴生对象实现类级别的属性和方法。
*   **底层代价/原理**：每一个 `companion object` 实际上都在外部类中生成了一个名叫 `Companion` 的静态内部类，并创建了一个单例对象。默认情况下，外部调用伴生对象方法时，会经过 `Companion.getInstance().method()` 的派发，存在微小的性能损耗。
*   **优化手段**：对于不需要多态的常量和静态方法，务必加上 `const` 或 `@JvmStatic`，使其在字节码层面退化为真正的 Java `static`，消除冗余的派发。

## 3. 数据类 (Data Class) 与解构声明 (Destructuring)
*   **现象**：`val (name, age) = user` 可以直接拆解对象。
*   **底层代价/原理**：`data class` 自动生成了 `component1()`, `component2()` 等方法。解构声明本质上就是按顺序调用这些 `componentN()` 方法。
*   **面试坑点**：解构完全依赖**声明顺序**！如果在 `data class` 中间插了一个新字段，所有使用解构的地方取值都会错位，且编译器不会报错。业务级 `data class` 要慎用解构，或确保字段仅在末尾追加。

## 4. 延迟初始化：lateinit vs by lazy
*   `lateinit var`：仅仅是告诉编译器“先别管非空检查，我保证以后会赋值”。底层就是个普通变量。
*   `val x by lazy { }`：底层创建了一个 `Lazy` 对象，默认使用**双重检查锁（DCL, Double Check Lock）**保证线程安全。
*   **优化手段**：如果在单线程环境（如仅在 UI 线程初始化的变量），应使用 `lazy(LazyThreadSafetyMode.NONE)` 来省去 `synchronized` 锁的巨大开销。