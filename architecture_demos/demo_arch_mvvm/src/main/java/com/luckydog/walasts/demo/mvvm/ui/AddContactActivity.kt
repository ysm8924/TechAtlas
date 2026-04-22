package com.luckydog.walasts.demo.mvvm.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.luckydog.walasts.demo.mvvm.databinding.ActivityAddContactBinding
import com.luckydog.walasts.demo.mvvm.viewmodel.AddContactViewModel
import kotlinx.coroutines.launch

/**
 * View 层：只负责 UI 展示和用户交互。
 * 演示：
 * 1. 使用 ViewBinding 减少 findViewById。
 * 2. 观察 ViewModel 暴露的 StateFlow 来更新 UI。
 * 3. 将点击事件直接透传给 ViewModel。
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
            val areaCode = binding.etAreaCode.text.toString()
            val phone = binding.etPhone.text.toString()
            val nickname = binding.etNickname.text.toString()
            
            // 将行为交给 ViewModel 处理
            viewModel.addContact(areaCode, phone, nickname)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 1. 观察 Loading 状态
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                        binding.btnSubmit.isEnabled = !isLoading
                    }
                }

                // 2. 观察 Toast 消息
                launch {
                    viewModel.toastMessage.collect { message ->
                        message?.let {
                            Toast.makeText(this@AddContactActivity, it, Toast.LENGTH_SHORT).show()
                            viewModel.clearToast()
                        }
                    }
                }
            }
        }
    }
}
