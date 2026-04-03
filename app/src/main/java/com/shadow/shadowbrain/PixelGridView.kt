// Responsibility: High-performance drawing with smart toggle/erase logic
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
    private val paint = Paint().apply { isFilterBitmap = false }
    private val rect = Rect()
    
    private var isErasing = false // Режим поточного дотику

    override fun onDraw(canvas: Canvas) {
        for (i in pixels.indices) {
            val color = if (pixels[i] == 1.0) Color.CYAN else Color.BLACK
            bitmap.setPixel(i % size, i / size, color)
        }
        rect.set(0, 0, width, height)
        canvas.drawBitmap(bitmap, null, rect, paint)
        
        // Малювання ліній сітки
        val p = Paint().apply { color = Color.DKGRAY; strokeWidth = 1f }
        for (i in 0..size) {
            val pos = i * (width.toFloat() / size)
            canvas.drawLine(pos, 0f, pos, height.toFloat(), p)
            canvas.drawLine(0f, pos, width.toFloat(), pos, p)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = (event.x / (width / size)).toInt().coerceIn(0, size - 1)
        val y = (event.y / (height / size)).toInt().coerceIn(0, size - 1)
        val index = y * size + x

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Визначаємо режим: якщо клікнули на зафарбоване — стираємо, інакше малюємо
                isErasing = pixels[index] == 1.0
                pixels[index] = if (isErasing) 0.0 else 1.0
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                pixels[index] = if (isErasing) 0.0 else 1.0
                invalidate()
            }
        }
        return true
    }

    fun getRawData() = pixels.copyOf()
    fun clear() { pixels.fill(0.0); invalidate() }
}
