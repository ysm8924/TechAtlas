package com.luckydog.walasts.demo.mvvm.model.repository

import com.luckydog.walasts.demo.mvvm.model.AddContactResult
import com.luckydog.walasts.demo.mvvm.model.Contact
import kotlinx.coroutines.delay

/**
 * Repository 层：负责数据获取逻辑。
 * 演示：将 Activity 中的网络请求和模拟校验逻辑收拢到这里。
 */
class ContactRepository {

    /**
     * 模拟注册联系人的网络请求
     */
    suspend fun registerContact(contact: Contact): AddContactResult {
        // 模拟网络耗时
        delay(1500)

        // 模拟业务逻辑：如果号码是 123456，模拟已存在错误
        if (contact.phoneNumber == "123456") {
            return AddContactResult.Error("Contact already exists (Simulated)")
        }

        // 模拟 429 频率限制
        if (contact.phoneNumber == "999") {
            return AddContactResult.NetworkError(429)
        }

        // 模拟成功
        return AddContactResult.Success
    }
}
