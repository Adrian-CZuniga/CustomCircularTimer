package com.example.customprogresstimer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.example.customcirculartimer.R

abstract class ProgressTimerView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
)  : TimerView(context, attrs, defStyleAttr) {

    protected val paintProgressBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
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

    protected var markerVisibility = true
    protected var markers: List<Long> = emptyList()

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ProgressTimerView,
            defStyleAttr,
            0
        ).apply {
            try {
                paintProgressBackground.color = getColor(R.styleable.ProgressTimerView_paintProgressColorBackground, Color.GRAY)
                paintProgress.color = getColor(R.styleable.ProgressTimerView_progressColor, Color.GREEN)
                paintMarker.color = getColor(R.styleable.ProgressTimerView_markerColor, Color.BLUE)
                painMarkerSecondary.color = getColor(R.styleable.ProgressTimerView_markerSecondaryColor, Color.YELLOW)

                paintProgress.strokeWidth = getDimension(R.styleable.ProgressTimerView_strokeWidth, 20f)
                painMarkerSecondary.strokeWidth = getDimension(R.styleable.ProgressTimerView_strokeWidth, 20f)
                paintMarker.strokeWidth = getDimension(R.styleable.ProgressTimerView_strokeWidth, 20f)
                paintProgressBackground.strokeWidth = getDimension(R.styleable.ProgressTimerView_strokeWidth, 20f)

            } finally {
                recycle()
            }
        }
    }

    var onValueReachedMarkerListener: OnValueReachedMarkerListener? = null

    protected abstract fun drawMarkerInShape(canvas: Canvas)
    protected abstract fun drawProgressBar(canvas: Canvas)

    fun setMarkersVisibility(visible: Boolean) {
        markerVisibility = visible
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawProgressBar(canvas)
        if (markerVisibility) {
            drawMarkerInShape(canvas)
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
