package com.ysm.techatlas.framework_learning.dagger2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import javax.inject.Inject
import javax.inject.Named

/**
 * 演示 Dagger 2 在 Android 环境下的基本使用。
 * 
 * 原理：
 * Dagger 2 是一个基于注解处理器的编译时依赖注入框架。
 * 它在编译时生成代码（例如 DaggerDemoComponent），负责创建和提供对象，从而避免了运行时的反射开销。
 * 
 * 核心概念：
 * 1. @Inject: 标记需要注入的变量或构造函数。
 * 2. @Module: 提供对象的工厂类，用于封装无法直接在构造函数上添加 @Inject 的类。
 * 3. @Provides: 在 @Module 中标记提供对象的方法。
 * 4. @Component: 注入器（桥梁），连接 @Module 和 @Inject。
 */
class DaggerDemoActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    @Named("WelcomeMessage")
    lateinit var welcomeMessage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 在 Android 中，通常在 onCreate 中进行注入。
        // DaggerDemoComponent 是由 Dagger 根据 DemoComponent 接口自动生成的。
        // 注意：如果编译未通过，DaggerDemoComponent 可能暂时无法识别，需要进行一次 Build。
        DaggerDemoComponent.builder()
            .demoModule(DemoModule())
            .build()
            .inject(this)

        Log.d("DaggerDemo", "Injected Welcome Message: $welcomeMessage")
        Log.d("DaggerDemo", "Injected User: ${userRepository.getUserName()}")
    }
}
