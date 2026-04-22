package com.luckydog.walasts.demo.mvi.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.luckydog.walasts.demo.mvi.contract.AddContactEffect
import com.luckydog.walasts.demo.mvi.contract.AddContactIntent
import com.luckydog.walasts.demo.mvi.contract.AddContactState
import com.luckydog.walasts.demo.mvi.databinding.ActivityAddContactBinding
import com.luckydog.walasts.demo.mvi.viewmodel.AddContactViewModel
import kotlinx.coroutines.launch

/**
 * MVI View 层:
 * 1. 所有的 UI 变化只来源于单一的 uiState。
 * 2. 所有的用户操作封装为 Intent 发送给 ViewModel。
 * 3. 所有的临时事件（Toast）通过 Effect 处理。
 */
class AddContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddContactBinding
    private val viewModel: AddContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
        observeViewModel()
    }

    private fun initListeners() {
        binding.btnSubmit.setOnClickListener {
            // 发送 Intent
            viewModel.handleIntent(
                AddContactIntent.Submit(
                    binding.etAreaCode.text.toString(),
                    binding.etPhone.text.toString(),
                    binding.etNickname.text.toString()
                )
            )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 1. 观察唯一状态流 (State)
                launch {
                    viewModel.uiState.collect { state ->
                        render(state)
                    }
                }

                // 2. 观察一次性副作用 (Effect)
                launch {
                    viewModel.effect.collect { effect ->
                        handleEffect(effect)
                    }
                }
            }
        }
    }

    /**
     * 渲染 UI：UI 是状态的纯函数 UI = f(State)
     */
    private fun render(state: AddContactState) {
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        binding.btnSubmit.isEnabled = state.isButtonEnabled
        
        if (state.success) {
            // 可以在这里处理成功后的复杂 UI 变化
        }
    }

    /**
     * 处理副作用
     */
    private fun handleEffect(effect: AddContactEffect) {
        when (effect) {
            is AddContactEffect.ShowToast -> {
                Toast.makeText(this, effect.message, Toast.LENGTH_SHORT).show()
            }
            is AddContactEffect.NavigateBack -> {
                // finish() // 演示用，暂不关闭
            }
        }
    }
}
