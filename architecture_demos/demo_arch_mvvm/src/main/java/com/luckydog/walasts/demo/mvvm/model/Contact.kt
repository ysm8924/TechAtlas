package com.luckydog.walasts.demo.mvvm.model

/**
 * 领域模型：联系人
 */
data class Contact(
    val areaCode: String,
    val phoneNumber: String,
    val nickname: String
)

/**
 * 封装注册结果的密封类
 */
sealed class AddContactResult {
    object Success : AddContactResult()
    data class Error(val message: String) : AddContactResult()
    data class NetworkError(val code: Int) : AddContactResult()
}
