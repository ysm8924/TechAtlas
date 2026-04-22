package com.luckydog.walasts.demo.ca.domain.usecase

import com.luckydog.walasts.demo.ca.domain.model.Contact
import com.luckydog.walasts.demo.ca.domain.model.DomainException
import com.luckydog.walasts.demo.ca.domain.repository.ContactRepository

/**
 * UseCase: 添加联系人用例
 * 职责：编排业务逻辑，验证业务规则。
 */
class AddContactUseCase(private val repository: ContactRepository) {

    suspend operator fun invoke(areaCode: String, phone: String, nickname: String) {
        // 1. 纯业务规则验证 (不依赖 UI)
        if (areaCode.isBlank() || phone.isBlank()) {
            throw DomainException.ValidationFailed
        }
        
        if (nickname.length > 30) {
            throw DomainException.ValidationFailed
        }

        // 2. 调用 Repository
        val contact = Contact(areaCode, phone, nickname)
        repository.addContact(contact)
    }
}
