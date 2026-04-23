package com.ysm.techatlas.framework_learning.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

/**
 * Hilt 模块必须使用 @InstallIn 注解，指定该模块的生命周期。
 * SingletonComponent 对应 Dagger 中的全局单例作用域。
 */
@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Provides
    @Singleton
    @Named("HiltMessage")
    fun provideHiltMessage(): String {
        return "Hello from Hilt Module!"
    }
}
