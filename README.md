# TechAtlas (技术图谱) 🗺️

欢迎来到 **TechAtlas**！
本项目是一个现代化的 Android 多模块工程底座，兼具**“生产级架构模板”**与**“技术实验室”**双重属性。

## 🎯 项目愿景

1. **知识沉淀 (Knowledge Base)**：将碎片化的 Android/Kotlin/Java 知识点、高频面试题、底层原理通过可运行的代码库沉淀下来。
2. **架构演进 (Architecture Sandbox)**：提供一个标准的现代化 Android 多模块架构环境，随时可用于测试新的 Jetpack 组件库、第三方框架或架构模式（MVI/MVVM等）。
3. **极速验证 (Fast Lab)**：剥离 Android 依赖的纯语言模块，能够以极快的速度在本地 JVM 验证语言特性和算法。

## 🏗️ 模块架构说明

当前项目基于 Gradle Version Catalog (`libs.versions.toml`) 与 Kotlin DSL 构建，划分为以下层次：

| 模块名称 | 类型 | 说明 |
| :--- | :--- | :--- |
| **`:app`** | Android App | 应用入口模块。包含核心 UI 层、Jetpack Compose 页面集成以及依赖注入的组装。 |
| **`:core:common`** | Android Lib | 核心通用库。存放跨模块复用的基础工具类、扩展函数、基础实体等。 |
| **`:core:network`** | Android Lib | 网络层基础库。用于封装 Retrofit/Ktor 等网络请求与 API 基础配置。 |
| **`:core:ui`** | Android Lib | UI 基础库。存放设计系统 (Design System)、通用的 Compose 组件、主题 (Theme) 及资源文件。 |
| **`:labs:kotlin_core`**| **纯 Kotlin** | Kotlin 语言实验室。**不依赖 Android SDK**，直接运行在本地 JVM。用于学习协程、Flow、内联函数、设计模式等。 |
| **`:labs:java_core`** | Android Lib | Java 语言/基础实验室。用于探究和对比 Java 特性机制。 |

---

> **附注**：本项目的学习与开发流，请详细阅读 [LEARNING_WORKFLOW.md](LEARNING_WORKFLOW.md)。