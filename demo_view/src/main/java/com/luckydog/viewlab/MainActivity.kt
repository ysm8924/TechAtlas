package com.luckydog.viewlab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.luckydog.walasts.view.CircleLoadingView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 可以通过代码找到 View 并进行一些操作
        val loadingView = findViewById<CircleLoadingView>(R.id.circleLoadingView)
        loadingView.startAnimation()
    }
}
