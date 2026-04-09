plugins {
    // 【核心修改】：移除 Android Library 插件，改为纯 Kotlin JVM 插件
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // 移除所有 Android 相关的依赖 (包括 androidx 和 core:common)
    // 仅保留纯 Kotlin 标准库和协程核心库，实现纯粹的 Kotlin 执行环境
    implementation(libs.kotlinx.coroutines.core)
}