# Kotlin 高级类型系统核心考点

Kotlin 的类型系统相比 Java 做了大量的现代化升级，尤其是引入了空安全、底层类型（Bottom Type）、声明处泛型型变以及零成本抽象的值类。这也是中高级 Android/Kotlin 工程师面试的重灾区。

## 1. 顶层、单值与底层类型
*   **`Any` / `Any?` (顶层类型)**：相当于 Java 的 `Object`。所有非空类型的超类是 `Any`，所有类型的最终超类是 `Any?`。
*   **`Unit` (单值类型)**：相当于 Java 的 `void`。不同于 `void` 只是一个关键字，`Unit` 是一个真正的对象（单例）。它表示“函数成功执行完毕，但没有有意义的返回值”。
*   **`Nothing` (底层类型 - Bottom Type)**：【高频面试题】
    *   **概念**：`Nothing` 是 Kotlin 中**所有类的子类**。它不能被实例化。
    *   **意义**：表示**“永远不会存在的值”**或**“正常的控制流到此中断”**。
    *   **场景**：抛出异常的函数 `throw Exception()` 的返回类型就是 `Nothing`；无限死循环也是 `Nothing`。正因为它是所有类的子类，所以 `val s: String = throw Exception()` 是合法的（子类可以赋值给父类引用）。

## 2. 泛型的协变与逆变 (Variance: out / in)
Java 中的泛型默认是**不型变 (Invariant)** 的，即 `List<String>` 不是 `List<Object>` 的子类。Java 通过 `? extends T` 和 `? super T` 在使用处解决这个问题。
Kotlin 引入了**声明处型变 (Declaration-site variance)**，让代码更简洁：
*   **`out T` (协变 Covariant)**：相当于 `? extends T`。
    *   **规则**：只能作为**输出**（返回值），不能作为输入（方法参数）。
    *   **记忆**：**Producer Extends, Consumer Super (PECS)**。`out` 就是 Producer（生产者），它只能往外产出数据，所以是安全的只读结构。
*   **`in T` (逆变 Contravariant)**：相当于 `? super T`。
    *   **规则**：只能作为**输入**（方法参数），不能作为输出。
    *   **记忆**：消费者（Consumer）。只能往里写，读出来全是 `Any?`。

## 3. 内联值类 (value class / inline class)
*   **现象**：为了类型安全，我们经常用一个类包装基本类型（例如 `class Password(val pwd: String)`），但这会带来堆内存分配（对象的 Header、指针等）的巨大开销。
*   **底层代价/原理**：使用 `@JvmInline value class`，在编译后，所有用到该包装类的地方都会被直接**替换为底层的基本类型**（如 `String`）。
*   **收益**：兼顾了“强类型的编译期安全”和“零对象分配的运行时极致性能”。
*   **考点限制**：目前一个 `value class` **只能且必须包含一个** val 属性，并且不能被继承（默认 final）。

## 4. 星号投影 (Star Projection: `*`)
当你对泛型的具体类型一无所知，或者根本不关心时使用，等价于 Java 的 `?` 通配符。
*   为了保证安全，如果泛型类是可读写的（如 `MutableList<T>`），被转化为 `MutableList<*>` 后，**读出来的类型降级为 `Any?`，且写入操作被严格禁止（等价于 in Nothing）**。这避免了向 Int 列表里误塞 String 的灾难。

## 5. 类型投影 (Type Projection: 使用处型变)
对于 Kotlin 原生数组 `Array<T>`，它是不可型变的（因为数组是可读可写的）。
如果你想把一个 `Array<Int>` 赋值给 `Array<Any>`，Kotlin 编译器会直接报错。
解决方案是：在**声明参数的地方**加上 `out` 或 `in`，例如 `fun copy(from: Array<out Any>)`，这等价于 Java 中的 `Array<? extends Object>`，在这一刻，你向编译器发誓“我只会从 from 里面读，绝不会往里面写”，编译器才会放行。