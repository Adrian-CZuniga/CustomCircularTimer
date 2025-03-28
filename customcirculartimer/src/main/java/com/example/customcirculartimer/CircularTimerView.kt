package com.example.customcirculartimer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat

class CircularTimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val tag = CircularTimerView::class.java.simpleName

    private val paintBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
    }
    private val paintProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
    }
    private val paintMarker = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 50f
        color = ContextCompat.getColor(context, android.R.color.black)
        textAlign = Paint.Align.CENTER
    }



    private var minValue: Long = 0L
    private var maxValue: Long = 10000L

     val duration : Long
        get() = maxValue - minValue

    private var increment : Long = 1000L
    private var formatText = TimeFormat.MM_SS

    private var currentValue: Long = 0L
    private var markers: List<Long> = emptyList()
    private val arcBounds = RectF()

    init {
        paintBackground.color = ContextCompat.getColor(context, android.R.color.darker_gray)
        paintProgress.color = ContextCompat.getColor(context, android.R.color.holo_blue_light)
        paintMarker.color = ContextCompat.getColor(context, android.R.color.holo_red_light)
        setIncrement()
        setCurrentValue(minValue)
    }
    var onValueReachedMarkerListener: OnValueReachedMarkerListener? = null


    fun setCurrentValue(value: Long){
        currentValue = value.coerceIn(minValue, maxValue)
        checkIfMarkerReached(currentValue)
        invalidate()
    }

    private fun checkIfMarkerReached(currentValue: Long) {
        for (marker in markers) {
            if (currentValue == marker) {
                onValueReachedMarkerListener?.onMarkerReached(marker)
                break
            }
        }
    }

    fun increase(){
        val newValue = currentValue + increment
        setCurrentValue(newValue)
    }

    fun decrease(){
        val newValue = currentValue - increment
        setCurrentValue(newValue)
    }

    fun setRange(min: Long, max: Long) {
        minValue = min
        maxValue = max
        currentValue = currentValue.coerceIn(min, max)
        invalidate()
    }

    fun setFormatText(format: TimeFormat){
        formatText = format
        invalidate()
    }

    fun setIncrement(newIncrement : Long = duration / 10) {
        increment = newIncrement
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

    fun setStrokeWidthTimer(width: Float) {
        paintBackground.strokeWidth = width
        paintProgress.strokeWidth = width
        paintMarker.strokeWidth = width / 4
        invalidate()
    }
    fun setMarkers(markers: List<Long>) {
        this.markers = markers
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = paintBackground.strokeWidth / 2
        arcBounds.set(padding, padding, w - padding, h - padding)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawArc(arcBounds, 0f, 360f, false, paintBackground)
        val sweepAngle = ((currentValue - minValue).toFloat() / (maxValue - minValue)) * 360f
        canvas.drawArc(arcBounds, -90f, sweepAngle, false, paintProgress)

        for (marker in markers) {
            if(marker in minValue..maxValue) {
                val markerAngle = ((marker - minValue).toFloat() / (maxValue - minValue)) * 360f
                canvas.drawArc(arcBounds, -90f + markerAngle, 2f, false, paintMarker)
            } else{
                Log.w(tag, "The marker with the value $marker is out of limits of the range.")
            }
        }

        val centerX = width / 2f
        val centerY = height / 2f - ((paintText.descent() + paintText.ascent()) / 2)
        canvas.drawText(currentValue.toTimeString(formatText), centerX, centerY, paintText)
    }

    interface OnValueReachedMarkerListener {
        fun onMarkerReached(marker: Long)
    }
}
