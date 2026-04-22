# Android 现代化架构指南 (MVVM / MVI / Clean Architecture)

本手册旨在对比分析 Android 开发中三种主流架构的设计思想、实现方式及应用场景，帮助团队在不同复杂度的业务中做出最佳选型。

---

## 1. 架构总览与核心思想

### MVVM (Model-View-ViewModel)
*   **核心思想**：响应式数据绑定 + 单向数据流 (UDF) 雏形。
*   **解决问题**：解决 Activity/Fragment 过重（Fat Activity）问题，通过 ViewModel 隔离 UI 和业务逻辑。
*   **特征**：ViewModel 不持有 View 引用，通过 LiveData/Flow 暴露数据，View 订阅变化。

### MVI (Model-View-Intent)
*   **核心思想**：不可变单向数据流 + 状态机 + 响应式编程。
*   **解决问题**：解决 MVVM 中多个数据流导致的“状态撕裂”和“难以追踪”问题。
*   **特征**：UI = f(State)。所有的交互封装为 Intent，所有的状态聚合为单一的 State。

### Clean Architecture (整洁架构)
*   **核心思想**：洋葱模型 + 依赖倒置。
*   **解决问题**：解决业务逻辑与框架（Android/DB/Network）深度耦合的问题。
*   **特征**：业务逻辑（Domain 层）位于核心，不依赖 Android SDK。

---

## 2. 典型的工程结构与命名

| 架构 | 典型目录结构 | 核心类命名规范 |
| :--- | :--- | :--- |
| **MVVM** | `ui/`, `viewmodel/`, `repository/`, `model/` | `UserActivity`, `UserViewModel`, `UserRepository` |
| **MVI** | `ui/`, `viewmodel/`, `contract/` (含 Intent/State/Effect) | `UserIntent`, `UserState`, `UserEffect`, `UserViewModel` |
| **CA** | `domain/` (UseCase/Entity), `data/` (RepoImpl), `presentation/` (UI/VM) | `GetUserUseCase`, `UserEntity`, `UserRepository` (Interface) |

---

## 3. 典型样板代码片段

### MVVM 模式
```kotlin
// ViewModel 暴露多个流
val userInfo = MutableLiveData<User>()
val isLoading = MutableLiveData<Boolean>()

// View 订阅
viewModel.userInfo.observe(this) { updateUI(it) }
```

### MVI 模式
```kotlin
// 唯一状态定义
data class UserState(val user: User? = null, val loading: Boolean = false)

// Intent 处理
fun handleIntent(intent: UserIntent) {
    when(intent) {
        is UserIntent.Refresh -> loadData()
    }
}
```

### Clean Architecture 模式
```kotlin
// UseCase 纯业务编排
class GetUserUseCase(private val repo: UserRepository) {
    suspend operator fun invoke(id: String): User = repo.getUser(id)
}
```

---

## 4. 关键术语解释

1.  **单向数据流 (UDF)**：
    数据始终沿着一个方向流动：`View -> Intent -> ViewModel -> State -> View`。保证了状态的可预测性。
2.  **状态撕裂 (State Splitting)**：
    在 MVVM 中，如果 `isLoading` 和 `data` 是独立的流，可能出现数据加载完了但 Loading 圈还没消失的短暂不同步。MVI 通过单一 State 对象彻底规避此问题。
3.  **副作用 (Side Effect)**：
    不改变状态的一次性操作。如：弹出一个 Toast、页面跳转、播放一段音效。
4.  **依赖倒置 (Dependency Inversion)**：
    CA 的核心。`Domain` 层定义接口，`Data` 层实现接口。让高层业务逻辑不依赖低层工具。

---

## 5. 选型指南与适用场景

| 架构方案 | 适用场景 | 选型建议 |
| :--- | :--- | :--- |
| **标准 MVVM** | 中小型项目、传统 XML 开发、逻辑相对线性的页面。 | **首选**。学习成本最低，开发速度快，适合大多数常规 App。 |
| **MVI** | 复杂交互页面（如视频编辑、多状态筛选列表）、使用 Jetpack Compose 的项目。 | **进阶推荐**。Compose 天然适配 MVI 思想。如果你发现页面状态变量超过 5 个且经常冲突，请切到 MVI。 |
| **Clean Arch** | 大型企业级项目、需要跨平台共享逻辑、核心业务规则极度复杂（如金融交易）。 | **长线必备**。如果项目生命周期预计 > 2 年，或者需要严格的单元测试覆盖，CA 是唯一选择。 |

---

## 6. 面试考点与高频问题

1.  **ViewModel 为什么能感知生命周期？**
    *   考察 `ViewModelStore` 和 `ComponentActivity` 的内部机制，以及为什么旋转屏幕后数据能保留。
2.  **MVI 的 State 为什么建议用 Data Class 配合 copy？**
    *   考察不可变性（Immutability）对调试和线程安全的价值。
3.  **Clean Architecture 中，Domain 层为什么不能包含 Context 或 Android 类？**
    *   考察对“纯 JVM 单元测试”意义的理解，以及架构边界（Boundary）的认知。
4.  **LiveData vs StateFlow 的区别？**
    *   考察对协程（Coroutines）的理解、粘性事件的处理方式以及热流/冷流的区别。
5.  **如何处理 MVI 中的一次性事件（Toast）？**
    *   考察对 `Channel` 和 `SharedFlow` 的应用场景理解。

---

**总结**：架构没有银弹。好的架构是根据团队规模、业务复杂度和项目周期动态平衡的结果。
