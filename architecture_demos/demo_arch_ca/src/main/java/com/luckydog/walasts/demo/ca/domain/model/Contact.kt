package com.luckydog.walasts.demo.ca.domain.model

/**
 * Domain Entity: 领域实体
 */
data class Contact(
    val areaCode: String,
    val phoneNumber: String,
    val nickname: String
)

/**
 * 业务异常
 */
sealed class DomainException(message: String) : Exception(message) {
    object ValidationFailed : DomainException("Invalid input data")
    object ContactAlreadyExists : DomainException("Contact already exists")
    data class Unknown(val originalMessage: String) : DomainException(originalMessage)
}
