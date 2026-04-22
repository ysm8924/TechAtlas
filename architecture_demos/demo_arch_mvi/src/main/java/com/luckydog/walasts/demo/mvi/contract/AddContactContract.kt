package com.luckydog.walasts.demo.mvi.contract

/**
 * MVI Contract: 定义 Intent, State 和 Effect
 */

// 1. Intent (用户意图)
sealed class AddContactIntent {
    data class Submit(val areaCode: String, val phone: String, val nickname: String) : AddContactIntent()
    object ClearToast : AddContactIntent()
}

// 2. State (UI 状态 - 唯一可信源)
data class AddContactState(
    val isLoading: Boolean = false,
    val isButtonEnabled: Boolean = true,
    val success: Boolean = false
)

// 3. Effect (一次性副作用：如 Toast, 导航)
sealed class AddContactEffect {
    data class ShowToast(val message: String) : AddContactEffect()
    object NavigateBack : AddContactEffect()
}
