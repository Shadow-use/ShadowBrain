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
    private val gridPaint = Paint().apply { color = Color.DKGRAY; strokeWidth = 1f }
    private var isErasing = false

    init { updateBitmap() }

    private fun updateBitmap() {
        for (i in pixels.indices) {
            bitmap.setPixel(i % size, i / size, if (pixels[i] == 1.0) Color.CYAN else Color.BLACK)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val rect = Rect(0, 0, width, height)
        canvas.drawBitmap(bitmap, null, rect, paint)
        
        val step = width.toFloat() / size
        for (i in 0..size) {
            canvas.drawLine(i * step, 0f, i * step, height.toFloat(), gridPaint)
            canvas.drawLine(0f, i * step, width.toFloat(), i * step, gridPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = (event.x / (width / size)).toInt().coerceIn(0, size - 1)
        val y = (event.y / (height / size)).toInt().coerceIn(0, size - 1)
        val idx = y * size + x

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isErasing = pixels[idx] == 1.0
                pixels[idx] = if (isErasing) 0.0 else 1.0
                updateBitmap()
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                val newVal = if (isErasing) 0.0 else 1.0
                if (pixels[idx] != newVal) {
                    pixels[idx] = newVal
                    updateBitmap()
                    invalidate()
                }
            }
        }
        return true
    }

    fun getRawData() = pixels.copyOf()
    fun clear() { pixels.fill(0.0); updateBitmap(); invalidate() }
}
