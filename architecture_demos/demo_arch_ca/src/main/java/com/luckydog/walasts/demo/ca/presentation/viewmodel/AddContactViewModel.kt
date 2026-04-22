package com.luckydog.walasts.demo.ca.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luckydog.walasts.demo.ca.data.repository.ContactRepositoryImpl
import com.luckydog.walasts.demo.ca.domain.usecase.AddContactUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Presentation Layer: ViewModel
 * 职责：转换领域数据为 UI 状态，将 UI 事件转换为 UseCase 调用。
 */
class AddContactViewModel : ViewModel() {

    // 依赖注入 (演示目的，暂不引入 Hilt/Koin)
    private val repository = ContactRepositoryImpl()
    private val addContactUseCase = AddContactUseCase(repository)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun addContact(areaCode: String, phone: String, nickname: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 直接执行用例
                addContactUseCase(areaCode, phone, nickname)
                _message.value = "Success (Clean Architecture)"
            } catch (e: Exception) {
                // 捕获领域层定义的异常
                _message.value = e.message ?: "Unknown Error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
