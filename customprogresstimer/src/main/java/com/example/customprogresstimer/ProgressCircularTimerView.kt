package com.example.customprogresstimer

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet

class ProgressCircularTimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ProgressTimerView(context, attrs, defStyleAttr) {
    private val tag = ProgressCircularTimerView::class.java.simpleName

    override var minValue: Long = 0L
    override var maxValue: Long = 10000L


    private val arcBounds = RectF()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = paintBackground.strokeWidth / 2
        arcBounds.set(padding, padding, w - padding, h - padding)
    }

    override fun drawShape(canvas: Canvas) {
        canvas.drawArc(arcBounds, 0f, 360f, false, paintBackground)
        val sweepAngle = ((currentValue - minValue).toFloat() / (maxValue - minValue)) * 360f
        canvas.drawArc(arcBounds, -90f, sweepAngle, false, paintProgress)
    }

    override fun drawMarkerInShape(canvas: Canvas) {
        for (marker in markers) {
            if (marker in minValue..maxValue) {
                val markerAngle = ((marker - minValue).toFloat() / (maxValue - minValue)) * 360f
                if (marker <= currentValue) {
                    canvas.drawArc(arcBounds, -90f + markerAngle, 4f, false, painMarkerSecondary)
                } else {
                    canvas.drawArc(arcBounds, -90f + markerAngle, 4f, false, paintMarker)
                }
            } else {
                markers = markers.filter { it in minValue..maxValue }
            }
        }
    }

}
