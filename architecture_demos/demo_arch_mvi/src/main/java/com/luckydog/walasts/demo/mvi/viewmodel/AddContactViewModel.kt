package com.luckydog.walasts.demo.mvi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luckydog.walasts.demo.mvi.contract.AddContactEffect
import com.luckydog.walasts.demo.mvi.contract.AddContactIntent
import com.luckydog.walasts.demo.mvi.contract.AddContactState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * MVI ViewModel: 核心是处理 Intent 并推导出新的 State。
 */
class AddContactViewModel : ViewModel() {

    // 唯一状态流
    private val _uiState = MutableStateFlow(AddContactState())
    val uiState: StateFlow<AddContactState> = _uiState.asStateFlow()

    // 一次性副作用通道 (Toast, Jump)
    private val _effect = Channel<AddContactEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /**
     * 处理 Intent 的统一入口
     */
    fun handleIntent(intent: AddContactIntent) {
        when (intent) {
            is AddContactIntent.Submit -> submitContact(intent)
            is AddContactIntent.ClearToast -> { /* MVI 下通常由 Effect 处理一次性事件 */ }
        }
    }

    private fun submitContact(intent: AddContactIntent.Submit) {
        // 1. 校验逻辑 (推导出副作用)
        if (intent.areaCode.isBlank()) {
            sendEffect(AddContactEffect.ShowToast("Area code required (MVI)"))
            return
        }

        // 2. 更新状态为 Loading (Reducer 思想：旧状态 -> 新状态)
        _uiState.update { it.copy(isLoading = true, isButtonEnabled = false) }

        viewModelScope.launch {
            // 模拟网络请求
            delay(1500)

            if (intent.phone == "123") {
                // 失败：更新状态并发送副作用
                _uiState.update { it.copy(isLoading = false, isButtonEnabled = true) }
                sendEffect(AddContactEffect.ShowToast("Simulated Error"))
            } else {
                // 成功：更新状态
                _uiState.update { it.copy(isLoading = false, success = true) }
                sendEffect(AddContactEffect.ShowToast("Success!"))
                sendEffect(AddContactEffect.NavigateBack)
            }
        }
    }

    private fun sendEffect(effect: AddContactEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
