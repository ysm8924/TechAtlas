package com.luckydog.walasts.demo.ca.data.repository

import com.luckydog.walasts.demo.ca.domain.model.Contact
import com.luckydog.walasts.demo.ca.domain.model.DomainException
import com.luckydog.walasts.demo.ca.domain.repository.ContactRepository
import kotlinx.coroutines.delay

/**
 * Data Layer: Repository 实现类
 * 负责具体的数据存取逻辑（DB, Network）。
 */
class ContactRepositoryImpl : ContactRepository {

    override suspend fun addContact(contact: Contact) {
        // 模拟网络请求
        delay(1500)
        
        // 模拟业务冲突
        if (contact.phoneNumber == "888") {
            throw DomainException.ContactAlreadyExists
        }
        
        // 模拟成功，无需返回结果，不抛异常即视为成功
    }
}
