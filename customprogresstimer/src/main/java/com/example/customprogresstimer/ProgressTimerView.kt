package com.example.customprogresstimer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

abstract class ProgressTimerView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val tag = ProgressCircularTimerView::class.java.simpleName

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

    init {
        paintBackground.color = ContextCompat.getColor(context, android.R.color.darker_gray)
        paintProgress.color = ContextCompat.getColor(context, android.R.color.holo_blue_light)
        paintMarker.color = ContextCompat.getColor(context, android.R.color.holo_red_light)
        painMarkerSecondary.color = ContextCompat.getColor(context, android.R.color.holo_green_light)

    }
    var onValueReachedMarkerListener: OnValueReachedMarkerListener? = null


    abstract fun drawShape(canvas: Canvas)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawShape(canvas)
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
