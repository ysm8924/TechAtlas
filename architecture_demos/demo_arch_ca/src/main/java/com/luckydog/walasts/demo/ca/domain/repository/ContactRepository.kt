package com.luckydog.walasts.demo.ca.domain.repository

import com.luckydog.walasts.demo.ca.domain.model.Contact

/**
 * Domain Repository Interface: 领域层定义的接口
 * 遵循依赖倒置原则（Dependency Inversion），外部层需要实现此接口。
 */
interface ContactRepository {
    suspend fun addContact(contact: Contact)
}
