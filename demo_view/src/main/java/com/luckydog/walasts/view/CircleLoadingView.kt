package com.luckydog.walasts.view

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.luckydog.viewlab.R
import kotlin.math.cos
import kotlin.math.sin

class CircleLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    private var circlePaint: Paint? = null
    private var circleColor = 0 // 圆的颜色
    private var circleCount = 0
    private var baseRadius = 0f // 最小圆半径 (px)
    private var radiusIncrement = 0f // 半径增量 (px)
    private var ringRadius = 0f // 圆环半径 (px)
    private var bgColor = 0 // 背景颜色

    private var currentRotationAngle = 0f
    private var rotationAnimator: ValueAnimator? = null

    init {
        init(context, attrs)
        isClickable = true
        isFocusable = true
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        // dp 转 px
        val density = resources.displayMetrics.density
        val defaultBaseRadiusPx = DEFAULT_BASE_RADIUS_DP * density
        val defaultRadiusIncrementPx = DEFAULT_RADIUS_INCREMENT_DP * density
        val defaultRingRadiusPx = DEFAULT_RING_RADIUS_DP * density

        // 加载自定义属性
        if (attrs != null) {
            val typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.CircleLoadingView)
            circleColor =
                typedArray.getColor(R.styleable.CircleLoadingView_clv_color, DEFAULT_COLOR)
            circleCount = typedArray.getInt(
                R.styleable.CircleLoadingView_clv_circleCount,
                DEFAULT_CIRCLE_COUNT
            )
            baseRadius = typedArray.getDimension(
                R.styleable.CircleLoadingView_clv_baseRadius,
                defaultBaseRadiusPx
            )
            radiusIncrement = typedArray.getDimension(
                R.styleable.CircleLoadingView_clv_radiusIncrement,
                defaultRadiusIncrementPx
            )
            ringRadius = typedArray.getDimension(
                R.styleable.CircleLoadingView_clv_ringRadius,
                defaultRingRadiusPx
            )
            bgColor =
                typedArray.getColor(R.styleable.CircleLoadingView_clv_bgColor, DEFAULT_BG_COLOR)
            // 确保 circleCount > 0
            if (circleCount <= 0) {
                circleCount = DEFAULT_CIRCLE_COUNT
            }
            typedArray.recycle()
        } else {
            // 设置默认值
            circleColor = DEFAULT_COLOR
            circleCount = DEFAULT_CIRCLE_COUNT
            baseRadius = defaultBaseRadiusPx
            radiusIncrement = defaultRadiusIncrementPx
            ringRadius = defaultRingRadiusPx
            bgColor = DEFAULT_BG_COLOR
        }


        // 初始化画笔
        circlePaint = Paint(Paint.ANTI_ALIAS_FLAG) // 开启抗锯齿
        circlePaint!!.color = circleColor
        circlePaint!!.style = Paint.Style.FILL

        // 初始化动画
        setupAnimator()
    }

    private fun setupAnimator() {
        if (rotationAnimator != null && rotationAnimator!!.isRunning) {
            rotationAnimator!!.cancel()
        }

        rotationAnimator = ValueAnimator.ofFloat(0f, 360f)
        rotationAnimator!!.setDuration(DEFAULT_ANIMATION_DURATION.toLong())
        rotationAnimator!!.setRepeatCount(ValueAnimator.INFINITE) // 无限循环
        rotationAnimator!!.setInterpolator(LinearInterpolator()) // 线性插值器，匀速旋转
        rotationAnimator!!.addUpdateListener(AnimatorUpdateListener { animation: ValueAnimator ->
            currentRotationAngle = animation.animatedValue as Float
            invalidate() // 请求重绘 View
        })
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // 计算 View 的建议尺寸
        val maxCircleRadius = baseRadius + (circleCount - 1) * radiusIncrement
        val desiredSize =
            ((ringRadius + maxCircleRadius) * 2 + paddingLeft + paddingRight + 0.5f).toInt() // 加上0.5f用于四舍五入

        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)
        setMeasuredDimension(width, height) // 设置最终尺寸
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // 先绘制背景颜色（如果设置了非透明色）
        if (Color.alpha(bgColor) > 0) {
            canvas.drawColor(bgColor)
        }

        // 获取视图中心点坐标 (考虑 padding)
        val centerX = (width - paddingLeft - paddingRight) / 2f + paddingLeft
        val centerY = (height - paddingTop - paddingBottom) / 2f + paddingTop

        // 计算每个小圆之间的角度间隔 - 改为 270 度，产生更强的头部和尾部感
        val angleStep = 270f / circleCount

        // 绘制每个小圆
        for (i in 0 until circleCount) {
            // 1. 半径线性增加：尾部小，头部大
            val currentCircleRadius = baseRadius + i * radiusIncrement
            
            // 2. 透明度线性增加：尾部淡，头部实 (解决类似原图的呼吸感)
            val alpha = ((i + 1).toFloat() / circleCount * 255).toInt()
            circlePaint!!.alpha = alpha

            // 计算当前圆的角度 (加上旋转角度)
            val angle = (i * angleStep + currentRotationAngle) % 360
            // 将角度转换为弧度
            val angleInRadians = Math.toRadians(angle.toDouble())

            // 计算当前圆心坐标
            val circleX = centerX + ringRadius * cos(angleInRadians).toFloat()
            val circleY = centerY + ringRadius * sin(angleInRadians).toFloat()

            // 绘制圆
            canvas.drawCircle(circleX, circleY, currentCircleRadius, circlePaint!!)
        }
    }

    // 控制动画开始
    fun startAnimation() {
        if (rotationAnimator != null && !rotationAnimator!!.isRunning) {
            // 确保在主线程启动动画
            post { rotationAnimator!!.start() }
        } else if (rotationAnimator == null) {
            setupAnimator()
            post { rotationAnimator!!.start() }
        }
    }

    // 控制动画停止
    fun stopAnimation() {
        if (rotationAnimator != null && rotationAnimator!!.isRunning) {
            rotationAnimator!!.cancel()
        }
    }

    // 当 View 附加到窗口时自动开始动画
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    // 当 View 从窗口分离时自动停止动画，防止内存泄漏
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
        rotationAnimator = null // 释放引用
    }

    // 当 View 的可见性改变时控制动画
    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {
            startAnimation()
        } else {
            stopAnimation()
        }
    }

    // --- 可选：提供 Setter 方法允许代码动态修改属性 ---
    fun setCircleColor(color: Int) {
        this.circleColor = color
        circlePaint!!.color = color
        invalidate() // 更新颜色后重绘
    }

    fun setCircleCount(count: Int) {
        if (count <= 0) return
        this.circleCount = count
        invalidate() // 更新数量后重绘
    }

    fun setBaseRadiusDp(dp: Float) {
        this.baseRadius = dp * resources.displayMetrics.density
        requestLayout() // 半径变化可能影响尺寸，请求重新测量布局
        invalidate()
    }

    fun setRadiusIncrementDp(dp: Float) {
        this.radiusIncrement = dp * resources.displayMetrics.density
        requestLayout() // 半径变化可能影响尺寸
        invalidate()
    }

    fun setRingRadiusDp(dp: Float) {
        this.ringRadius = dp * resources.displayMetrics.density
        requestLayout() // 半径变化可能影响尺寸
        invalidate()
    }

    companion object {
        private const val DEFAULT_CIRCLE_COUNT = 8
        private const val DEFAULT_BASE_RADIUS_DP = 1 // 最小圆的半径 (dp)
        private const val DEFAULT_RADIUS_INCREMENT_DP = 0.8f // 半径增量 (dp)
        private const val DEFAULT_RING_RADIUS_DP = 20 // 圆环中心到小圆中心的距离 (dp)
        private const val DEFAULT_COLOR = 0xFF11D344.toInt() // 亮绿色
        private const val DEFAULT_BG_COLOR = 0x00000000 // 默认透明
        private const val DEFAULT_ANIMATION_DURATION = 1000 // 旋转一圈的时间 (ms)
    }
}
