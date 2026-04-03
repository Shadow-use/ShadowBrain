// Responsibility: High-performance drawing canvas using a single View
package com.shadow.shadowbrain.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class PixelGridView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val size = 16
    private val pixels = DoubleArray(size * size) { 0.0 }
    private val paint = Paint().apply { style = Paint.Style.FILL }
    private val strokePaint = Paint().apply { 
        color = Color.GRAY
        style = Paint.Style.STROKE
        strokeWidth = 1f 
    }

    override fun onDraw(canvas: Canvas) {
        val cellW = width.toFloat() / size
        val cellH = height.toFloat() / size

        for (i in pixels.indices) {
            val x = (i % size) * cellW
            val y = (i / size) * cellH
            
            paint.color = if (pixels[i] == 1.0) Color.CYAN else Color.BLACK
            canvas.drawRect(x, y, x + cellW, y + cellH, paint)
            canvas.drawRect(x, y, x + cellW, y + cellH, strokePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
            val x = (event.x / (width / size)).toInt().coerceIn(0, size - 1)
            val y = (event.y / (height / size)).toInt().coerceIn(0, size - 1)
            pixels[y * size + x] = 1.0 // Малюємо. Для ластика можна додати флаг.
            invalidate()
        }
        return true
    }

    fun getRawData() = pixels.copyOf()
    fun clear() { pixels.fill(0.0); invalidate() }
}
