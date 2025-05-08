package com.example.customprogresstimer

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet

class TextTimerView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    TimerView(context, attrs, defStyleAttr) {
    private val rectBounds = RectF()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = paintBackground.strokeWidth / 2
        rectBounds.set(padding, padding, w - padding, h - padding)
    }
    override fun drawShape(canvas: Canvas) {
        canvas.drawRoundRect(rectBounds, cornerRadius, cornerRadius, paintBackground)
    }
}