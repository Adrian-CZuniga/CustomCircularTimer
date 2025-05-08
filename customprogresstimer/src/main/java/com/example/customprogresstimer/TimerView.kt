package com.example.customprogresstimer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.customcirculartimer.R
import kotlin.math.pow

abstract class TimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr){
    var isShowingIcon = false
    var isStarted = false
    var cornerRadius = 30f
        set(value) {
            field = value
            invalidate()
        }

    protected val paintBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.GRAY

    }
    protected val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 50f
        color = ContextCompat.getColor(context, android.R.color.black)
        textAlign = Paint.Align.CENTER
    }

    protected val paintStateIcon = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, android.R.color.black)
    }

    var minValue: Long = 0L
    var maxValue: Long = 10000L

    val duration : Long
        get() = maxValue - minValue

    var increment : Long = duration / 10
    var formatText = TimeFormat.MM_SS
        set(value) {
            field = value
            invalidate()
        }

    var currentValue: Long = 0L
        set(value) {
            field = value.coerceIn(minValue, maxValue)
            invalidate()
        }

    var stateIconVisibility = true
    var iconPauseDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.baseline_pause_circle_24)
    var iconPlayDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.baseline_play_circle_24)

    private var iconAlpha: Float = 1.0f
    private val fadeAnimator = ValueAnimator.ofFloat(1.0f, 1.0f, 0.0f).apply {
        duration = 1000
        addUpdateListener {
            iconAlpha = it.animatedValue as Float
            invalidate()
        }
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                isShowingIcon = true
            }
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                isShowingIcon = false
                iconAlpha = 1.0f
                invalidate()
            }
        })
    }
    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TimerView,
            defStyleAttr,
            0
        ).apply {
            try {
                paintBackground.color = getColor(R.styleable.TimerView_backgroundTimerViewColor, Color.TRANSPARENT)
                paintText.color = getColor(R.styleable.TimerView_textTimerViewColor, Color.BLACK)
                paintStateIcon.color = getColor(R.styleable.TimerView_iconStateTint, Color.GRAY)
                stateIconVisibility = getBoolean(R.styleable.TimerView_stateIconVisibility, true)
                cornerRadius = getFloat(R.styleable.TimerView_cornerTimerViewRadius, 30f)
            } finally {
                recycle()
            }

            // Yes, this line is necessary.
            setOnClickListener(null)

            iconPauseDrawable?.setTint(paintStateIcon.color)
            iconPlayDrawable?.setTint(paintStateIcon.color)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawShape(canvas)
        drawTimerText(canvas)
        if(isShowingIcon) {
            if (isStarted) {
                iconPauseDrawable?.let { drawStateIcon(canvas, it) }
            } else {
                iconPlayDrawable?.let { drawStateIcon(canvas, it) }
            }
        }
    }

    protected open fun drawShape(canvas: Canvas){
        // If need draw a background
    }

    protected open fun drawTimerText(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f - ((paintText.descent() + paintText.ascent()) / 2)
        canvas.drawText(currentValue.toTimeString(formatText), centerX, centerY, paintText)
    }


    fun increase() {
        val newValue = (currentValue + increment).coerceIn(minValue, maxValue)
        if (newValue != currentValue) {
            currentValue = newValue
            invalidate()
        }
    }

    fun decrease() {
        val newValue = (currentValue - increment).coerceIn(minValue, maxValue)
        if (newValue != currentValue) {
            currentValue = newValue
            invalidate()
        }
    }

    fun setRange(min: Long, max: Long) {
        minValue = min
        maxValue = max
        increment = (max - min) / 10
        currentValue = currentValue.coerceIn(min, max)
        invalidate()
    }

    override fun performClick(): Boolean {
        onBaseClick()
        return super.performClick()
    }
    protected open fun onBaseClick() {
        isStarted = !isStarted
        if (stateIconVisibility) {
            fadeAnimator.start()
        }
    }

    private fun drawStateIcon(canvas: Canvas, drawable: Drawable){
        drawable.let {
            val drawableSize = ((width * height).toDouble()).pow(0.425).toFloat()

            val centerX = (width - drawableSize) / 2f
            val centerY = (height - drawableSize) / 2f

            drawable.setBounds(
                centerX.toInt(),
                centerY.toInt(),
                (centerX + drawableSize).toInt(),
                (centerY + drawableSize).toInt()
            )

            drawable.alpha = (iconAlpha * 255).toInt()

            drawable.draw(canvas)
        }
    }
}