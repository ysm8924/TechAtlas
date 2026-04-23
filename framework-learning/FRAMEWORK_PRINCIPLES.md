# Android 核心框架原理与面试指南

## 1. Dagger 2

### 核心原理
Dagger 2 是基于 **APT (Annotation Processing Tool)** 的编译时依赖注入框架。
- **编译时生成代码**：在编译阶段扫描注解（@Inject, @Module, @Component 等），自动生成负责实例化对象和注入依赖的 Java 类（如 `DaggerXXXComponent`）。
- **无反射**：由于对象实例化和字段赋值都是通过生成的代码直接完成的，因此没有运行时的反射开销，性能极高。
- **有向无环图 (DAG)**：Dagger 在编译时会构建一个依赖关系图，检测是否存在循环依赖。

### 常见面试考点
1. **Dagger 2 为什么比 Guice 快？**
   - Guice 在运行时使用反射和动态代理来注入依赖，而 Dagger 2 在编译时生成代码，运行时是直接的 Java 调用。
2. **@Inject 和 @Provides 的区别？**
   - `@Inject` 用于标记构造函数（让 Dagger 知道如何创建该类）或字段（标记需要注入的地方）。
   - `@Provides` 用于 `@Module` 中，当一个类无法通过构造函数注入（如第三方库、接口或需要复杂配置的对象）时，手动定义创建逻辑。
3. **Component 和 Subcomponent 的区别？**
   - `Component` 是独立的注入器。
   - `Subcomponent` 是父组件的子组件，可以继承父组件的所有依赖，常用于 Activity 级别继承 Application 级别的依赖。

---

## 2. Hilt

### 核心原理
Hilt 是 Google 推出的基于 Dagger 2 的 **Jetpack 扩展库**，旨在简化 Android 中的依赖注入。
- **预定义组件**：Hilt 预定义了一套标准组件（如 `SingletonComponent`, `ActivityComponent`, `ViewModelComponent`），并自动管理它们的生命周期。
- **字节码插桩 (Bytecode Manipulation)**：Hilt 插件在编译期通过字节码插桩技术，自动为标记了 `@AndroidEntryPoint` 的类生成 Dagger Component 和相关的模板代码，开发者不再需要手动编写注入逻辑。
- **完全集成 Jetpack**：原生支持 ViewModel、WorkManager 等组件。

### 常见面试考点
1. **Hilt 和 Dagger 2 的关系？**
   - Hilt 是 Dagger 2 的上层封装。Hilt 并没有取代 Dagger，而是通过约定优于配置（Convention over Configuration）的方式，隐藏了 Dagger 的复杂配置。
2. **Hilt 的生命周期是如何管理的？**
   - Hilt 的组件（Component）与 Android 的生命周期绑定。例如，`ActivityComponent` 会在 Activity 的 `onCreate` 中创建，在 `onDestroy` 中销毁。注入的对象可以被标记为 `@ActivityScoped` 以保证在同一 Activity 实例中唯一。
3. **@AndroidEntryPoint 做了什么？**
   - 它是一个“魔术”注解。在编译时，Hilt 插件会修改该类的继承关系（例如让 `MyActivity` 继承自生成的 `Hilt_MyActivity`），并在基类中处理 Dagger 组件的创建和注入逻辑。

---

## 3. 面试通用问题：为什么需要依赖注入 (DI)？
- **解耦**：类不再负责管理其依赖项的生命周期。
- **可测试性**：可以轻松地将真实依赖替换为 Mock 对象进行单元测试。
- **重用性**：同一个模块可以在不同的上下文中重用，只需配置不同的依赖实现。
