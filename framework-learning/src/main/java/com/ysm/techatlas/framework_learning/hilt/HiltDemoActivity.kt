package com.ysm.techatlas.framework_learning.hilt

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

/**
 * Hilt 核心注解：@AndroidEntryPoint
 * 它会自动为 Activity 生成对应的注入代码，不再需要手动编写 Component 接口和调用 inject()。
 */
@AndroidEntryPoint
class HiltDemoActivity : ComponentActivity() {

    @Inject
    lateinit var repository: HiltRepository

    @Inject
    @Named("HiltMessage")
    lateinit var hiltMessage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hilt 会在 super.onCreate(savedInstanceState) 中自动完成注入
        
        Log.d("HiltDemo", "Message: $hiltMessage")
        Log.d("HiltDemo", "Data: ${repository.getHiltData()}")
    }
}
