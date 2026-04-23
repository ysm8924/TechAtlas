package com.ysm.techatlas.framework_learning.dagger2

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DemoModule::class])
interface DemoComponent {
    fun inject(activity: DaggerDemoActivity)
}
