package com.luckydog.walasts.demo.mvvm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luckydog.walasts.demo.mvvm.model.AddContactResult
import com.luckydog.walasts.demo.mvvm.model.Contact
import com.luckydog.walasts.demo.mvvm.model.repository.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel 层：持有 UI 状态并处理用户行为。
 * 演示：
 * 1. 使用 StateFlow 暴露 UI 状态。
 * 2. 处理表单校验逻辑。
 * 3. 使用协程处理异步请求。
 */
class AddContactViewModel : ViewModel() {

    private val repository = ContactRepository()

    // UI 状态：Loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // UI 状态：Toast 消息 (用于一次性事件)
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // UI 状态：操作结果
    private val _operationResult = MutableStateFlow<AddContactResult?>(null)
    val operationResult: StateFlow<AddContactResult?> = _operationResult.asStateFlow()

    /**
     * 执行添加联系人逻辑
     */
    fun addContact(areaCode: String, phone: String, nickname: String) {
        // 1. 基础校验 (原 Activity 中的逻辑搬迁至此)
        if (areaCode.isBlank()) {
            _toastMessage.value = "Please enter area code"
            return
        }
        if (phone.isBlank()) {
            _toastMessage.value = "Please enter phone number"
            return
        }
        if (nickname.length > 30) {
            _toastMessage.value = "Nickname too long"
            return
        }

        // 2. 发起请求
        viewModelScope.launch {
            _isLoading.value = true
            _toastMessage.value = null
            
            val contact = Contact(areaCode, phone, nickname)
            val result = repository.registerContact(contact)
            
            _operationResult.value = result
            _isLoading.value = false
            
            // 处理结果通知
            when (result) {
                is AddContactResult.Success -> _toastMessage.value = "Add Success!"
                is AddContactResult.Error -> _toastMessage.value = result.message
                is AddContactResult.NetworkError -> {
                    if (result.code == 429) {
                        _toastMessage.value = "Too frequent, try later"
                    } else {
                        _toastMessage.value = "Network Error: ${result.code}"
                    }
                }
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }
}
