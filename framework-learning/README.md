# Framework Learning Module

该模块用于学习和演示各种常用框架的使用与原理。

## 目录结构

每个框架都在独立的包下进行演示：

- `dagger2`: Dagger 2 依赖注入框架的演示。
  - `UserRepository`: 演示构造函数注入。
  - `DemoModule`: 演示通过 Module 提供依赖。
  - `DemoComponent`: 演示注入器接口。
  - `DaggerDemoActivity`: 演示在 Android 组件中如何使用 Dagger 2。

## 使用说明

由于 Dagger 2 是编译时生成代码，在添加新注入点后，请执行 `Build -> Rebuild Project` 以生成相关的 `DaggerXXX` 类。
