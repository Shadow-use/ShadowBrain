// Responsibility: High-performance drawing via Bitmap scaling
package com.shadow.shadowbrain.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class PixelGridView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val size = 16
    private val pixels = DoubleArray(size * size) { 0.0 }
    private val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    private val paint = Paint().apply { isFilterBitmap = false } // Sharp pixels
    private val rect = Rect()

    override fun onDraw(canvas: Canvas) {
        // Оновлюємо бітмап згідно масиву pixels
        for (i in pixels.indices) {
            val color = if (pixels[i] == 1.0) Color.CYAN else Color.BLACK
            bitmap.setPixel(i % size, i / size, color)
        }
        
        rect.set(0, 0, width, height)
        canvas.drawBitmap(bitmap, null, rect, paint)
        
        // Малюємо сітку поверх одним проходом (опціонально)
        drawGridLines(canvas)
    }

    private fun drawGridLines(canvas: Canvas) {
        val p = Paint().apply { color = Color.DKGRAY; strokeWidth = 1f }
        for (i in 0..size) {
            val pos = i * (width.toFloat() / size)
            canvas.drawLine(pos, 0f, pos, height.toFloat(), p)
            canvas.drawLine(0f, pos, width.toFloat(), pos, p)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
            val x = (event.x / (width / size)).toInt().coerceIn(0, size - 1)
            val y = (event.y / (height / size)).toInt().coerceIn(0, size - 1)
            pixels[y * size + x] = 1.0
            invalidate()
        }
        return true
    }

    fun getRawData() = pixels.copyOf()
    fun clear() { pixels.fill(0.0); invalidate() }
}
