package com.luckydog.walasts.demo.ca.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.luckydog.walasts.demo.ca.databinding.ActivityAddContactBinding
import com.luckydog.walasts.demo.ca.presentation.viewmodel.AddContactViewModel
import kotlinx.coroutines.launch

/**
 * Presentation Layer: Activity
 * 仅仅负责 UI 渲染。
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
            viewModel.addContact(
                binding.etAreaCode.text.toString(),
                binding.etPhone.text.toString(),
                binding.etNickname.text.toString()
            )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                        binding.btnSubmit.isEnabled = !isLoading
                    }
                }

                launch {
                    viewModel.message.collect { msg ->
                        msg?.let {
                            Toast.makeText(this@AddContactActivity, it, Toast.LENGTH_SHORT).show()
                            viewModel.clearMessage()
                        }
                    }
                }
            }
        }
    }
}
