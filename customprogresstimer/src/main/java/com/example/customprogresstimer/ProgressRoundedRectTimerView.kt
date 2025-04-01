package com.example.customprogresstimer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF
import android.util.AttributeSet
import java.util.concurrent.TimeUnit
import kotlin.math.atan2

class ProgressRoundedRectTimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ProgressTimerView(context, attrs, defStyleAttr) {
    private val rectBounds = RectF()
    private var cornerRadius = 30f

    override var minValue: Long = 0L
    override var maxValue: Long = 1000L



    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = paintBackground.strokeWidth / 2
        rectBounds.set(padding, padding, w - padding, h - padding)
    }

    override fun drawShape(canvas: Canvas) {
        canvas.drawRoundRect(rectBounds, cornerRadius, cornerRadius, paintBackground)

        val outlinePath = Path().apply {
            addRoundRect(rectBounds, cornerRadius, cornerRadius, Path.Direction.CW)
        }

        val pathMeasure = PathMeasure(outlinePath, false)
        val totalLength = pathMeasure.length

        val centerX = rectBounds.centerX()
        val centerY = rectBounds.centerY()

        val pos0 = FloatArray(2)
        pathMeasure.getPosTan(0f, pos0, null)
        val currentAngle = Math.toDegrees(atan2((pos0[1] - centerY).toDouble(), (pos0[0] - centerX).toDouble())).toFloat()

        val desiredAngle = -90f
        var angleDiff = desiredAngle - currentAngle
        while (angleDiff < 0) angleDiff += 360f
        while (angleDiff >= 360f) angleDiff -= 360f

        val offsetDistance = (angleDiff / 360f) * totalLength

        val progressFraction = ((currentValue - minValue).toFloat() / (maxValue - minValue)).coerceIn(0f, 1f)
        val segmentLength = totalLength * progressFraction

        val progressPath = Path()
        var startDistance = offsetDistance
        var endDistance = offsetDistance + segmentLength

        if (endDistance <= totalLength) {
            pathMeasure.getSegment(startDistance, endDistance, progressPath, true)
        } else {
            pathMeasure.getSegment(startDistance, totalLength, progressPath, true)
            val remainder = endDistance - totalLength
            val tempPath = Path()
            pathMeasure.getSegment(0f, remainder, tempPath, true)
            progressPath.addPath(tempPath)
        }

        canvas.drawPath(progressPath, paintProgress)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val outlinePath = Path().apply {
            addRoundRect(rectBounds, cornerRadius, cornerRadius, Path.Direction.CW)
        }
        val pathMeasure = PathMeasure(outlinePath, false)
        val totalLength = pathMeasure.length

        val offset = totalLength * (90f / 360f)

        for (marker in markers) {
            if (marker in minValue..maxValue) {

            }
        }

        // Dibujar el texto centrado
        val centerX = width / 2f
        val centerY = height / 2f - ((paintText.descent() + paintText.ascent()) / 2)
        canvas.drawText(currentValue.toTimeString(formatText), centerX, centerY, paintText)
    }

    fun setCornerRadius(radius: Float) {
        cornerRadius = radius
        invalidate()
    }
}