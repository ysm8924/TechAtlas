# Kotlin 现代化类系统 (Class Types) 核心考点

Kotlin 提供了多种高度定制的类声明方式，用以应对不同的软件设计模式和场景。这些语法糖在底层都会被编译为特定的 Java 字节码结构。建议结合 Android Studio 的 **Decompile** 功能观察底层实现。

## 1. 数据类 (Data Class)
*   **定位**：纯粹的数据载体（POJO/DTO 的现代替代品）。
*   **底层生成**：编译器自动根据主构造函数中声明的属性，生成 `equals()`, `hashCode()`, `toString()`, `copy()`, 以及用于解构的 `componentN()` 方法。
*   **高频面试题**：
    *   **限制**：不能被继承（默认 final 且不能 open），也不能是 `abstract`, `open`, `sealed` 或 `inner`。
    *   **数组陷阱**：如果 data class 的属性包含 `Array`，自动生成的 `equals()` 会比较数组引用而不是内容。必须手动重写 `equals()` 和 `hashCode()`。
    *   **非主构造属性**：在类体 `{}` 中定义的属性，**不会**参与自动生成的 `equals/hashCode/toString/copy`。

## 2. 单例与对象声明 (Object)
*   **定位**：Kotlin 中替代 Java `static` 与单例模式的核心关键字。
*   **三大形态**：
    1.  **对象声明 (`object MySingleton`)**：懒汉式单例（线程安全）。底层通过静态代码块 `static { INSTANCE = new ... }` 在类加载时初始化。
    2.  **伴生对象 (`companion object`)**：在类内部定义的单例。底层生成内部类 `Companion`，外部通过外部类名直接调用。
    3.  **对象表达式 (`object : Runnable`)**：替代 Java 的匿名内部类。

## 3. 密封类 (Sealed Class) 与 密封接口 (Sealed Interface)
*   **定位**：受限的类继承层次结构（Sum Type / 代数数据类型）。
*   **对比 Enum**：枚举 (`enum`) 的每个实例是全局唯一的，且不能拥有独立的状态；而 `sealed class` 的子类可以有无数个实例，且每个子类可以有完全不同的属性。
*   **底层生成**：`sealed class` 编译后是一个 `abstract class`（抽象类），它的构造函数被魔改为包私有或者带有一个特定的标记参数（Synthetic），从而在 JVM 层面阻止外部包的类继承它。
*   **核心优势**：在 `when` 表达式中，编译器能做到**穷举检查 (Exhaustive Check)**。如果不写 `else` 分支，漏掉某个子类时会直接编译报错，极大提升了业务代码的安全性（常用于 MVI 架构中的 State/Intent 定义）。