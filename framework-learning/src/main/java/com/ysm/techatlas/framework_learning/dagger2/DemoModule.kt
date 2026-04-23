package com.ysm.techatlas.framework_learning.dagger2

import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class DemoModule {
    
    @Provides
    @Named("WelcomeMessage")
    fun provideWelcomeMessage(): String {
        return "Welcome to Dagger 2 Learning!"
    }
}
