package com.ysm.techatlas.framework_learning.dagger2

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {
    fun getUserName(): String {
        return "Dagger2 User"
    }
}
