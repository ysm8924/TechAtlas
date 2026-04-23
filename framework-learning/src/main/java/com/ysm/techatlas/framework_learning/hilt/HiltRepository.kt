package com.ysm.techatlas.framework_learning.hilt

import javax.inject.Inject

/**
 * Hilt 依然使用 @Inject 进行构造函数注入。
 */
class HiltRepository @Inject constructor() {
    fun getHiltData(): String {
        return "Data from Hilt Repository"
    }
}
