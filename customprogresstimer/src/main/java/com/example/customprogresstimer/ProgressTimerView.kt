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

abstract class ProgressTimerView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var isShowingIcon = false
    var isStarted = false

    protected val paintBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
    }
    protected val paintProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
    }
    protected val paintMarker = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
    }

    protected val painMarkerSecondary = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
    }
    protected val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 50f
        color = ContextCompat.getColor(context, android.R.color.black)
        textAlign = Paint.Align.CENTER
    }
    protected var markerVisibility = true

    abstract var minValue: Long
    abstract var maxValue: Long

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
            checkIfMarkerReached(currentValue)
            invalidate()
        }
    protected var markers: List<Long> = emptyList()

    var stateIconVisibility = true
    var iconPauseDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.baseline_pause_circle_24)
    var iconPlayDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.baseline_play_circle_24)

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ProgressTimerView,
            defStyleAttr,
            0
        ).apply {
            try {
                paintProgress.color = getColor(R.styleable.ProgressTimerView_progressColor, Color.RED)
                paintBackground.color = getColor(R.styleable.ProgressTimerView_backgroundTimerColor, Color.GRAY)
                paintMarker.color = getColor(R.styleable.ProgressTimerView_markerColor, Color.BLUE)
                painMarkerSecondary.color = getColor(R.styleable.ProgressTimerView_markerSecondaryColor, Color.YELLOW)

                paintText.color = getColor(R.styleable.ProgressTimerView_timerTextColor, Color.BLACK)
                paintProgress.strokeWidth = getDimension(R.styleable.ProgressTimerView_strokeWidth, 20f)
                painMarkerSecondary.strokeWidth = getDimension(R.styleable.ProgressTimerView_strokeWidth, 20f)
                paintMarker.strokeWidth = getDimension(R.styleable.ProgressTimerView_strokeWidth, 20f)
                paintBackground.strokeWidth = getDimension(R.styleable.ProgressTimerView_strokeWidth, 20f)

            } finally {
                recycle()
            }

            // Yes, this line is necessary.
            setOnClickListener(null)

            iconPauseDrawable?.setTint(paintBackground.color)
            iconPlayDrawable?.setTint(paintBackground.color)
        }
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
    var onValueReachedMarkerListener: OnValueReachedMarkerListener? = null


    protected abstract fun drawShape(canvas: Canvas)

    protected abstract fun drawMarkerInShape(canvas: Canvas)

    protected open fun drawTimerText(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f - ((paintText.descent() + paintText.ascent()) / 2)
        canvas.drawText(currentValue.toTimeString(formatText), centerX, centerY, paintText)
    }

    fun setMarkersVisibility(visible: Boolean) {
        markerVisibility = visible
        invalidate()
    }

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

    fun drawStateIcon(canvas: Canvas, drawable: Drawable){
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


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawShape(canvas)
        if (markerVisibility) {
            drawMarkerInShape(canvas)
        }
        drawTimerText(canvas)

        if(isShowingIcon) {
            if (isStarted) {
                iconPauseDrawable?.let { drawStateIcon(canvas, it) }
            } else {
                iconPlayDrawable?.let { drawStateIcon(canvas, it) }
            }
        }
    }

    private fun checkIfMarkerReached(currentValue: Long) {
        for (marker in markers) {
            if (currentValue == marker) {
                onValueReachedMarkerListener?.onMarkerReached(marker)
                break
            }
        }
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


    fun setProgressColor(color: Int) {
        paintProgress.color = color
        invalidate()
    }

    fun setBackgroundTimerColor(color: Int) {
        paintBackground.color = color
        invalidate()
    }

    fun setMarkerColor(color: Int) {
        paintMarker.color = color
        invalidate()
    }

    fun setMarkerSecondaryColor(color: Int) {
        painMarkerSecondary.color = color
        invalidate()
    }

    fun attachMarkersByDivider(divider: Int) {
        val newMarkers = mutableListOf<Long>()
        for (i in 1 until divider) {
            newMarkers.add(minValue + (duration / divider) * i)
        }
        markers = newMarkers
        invalidate()
    }

    fun setStrokeWidthTimer(width: Float) {
        paintBackground.strokeWidth = width
        paintProgress.strokeWidth = width
        paintMarker.strokeWidth = width
        painMarkerSecondary.strokeWidth = width
        invalidate()
    }
    fun attachMarkers(markers: List<Long>) {
        this.markers = markers
        invalidate()
    }

    interface OnValueReachedMarkerListener {
        fun onMarkerReached(marker: Long)
    }
}
