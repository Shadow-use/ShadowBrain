// Responsibility: Керування сіткою 16x16 для ручного малювання та перевірки
package com.shadow.shadowbrain

import android.graphics.Color
import android.view.MotionEvent
import android.widget.Button
import android.widget.GridLayout

class UIController(private val grid: GridLayout) {
    private val size = 16
    private val cells = DoubleArray(size * size) { 0.0 }
    private val buttons = mutableListOf<Button>()

    init {
        grid.columnCount = size
        grid.rowCount = size
        val ctx = grid.context
        
        for (i in 0 until size * size) {
            val btn = Button(ctx).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 40 // Фіксований розмір для стабільності на мобільному
                    height = 40
                }
                setBackgroundColor(Color.BLACK)
                // Малювання свайпом (Touch)
                setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_DOWN) {
                        cells[i] = 1.0
                        v.setBackgroundColor(Color.CYAN)
                    }
                    true
                }
            }
            buttons.add(btn)
            grid.addView(btn)
        }
    }

    fun getInput() = cells.copyOf()
    
    fun clear() {
        cells.fill(0.0)
        buttons.forEach { it.setBackgroundColor(Color.BLACK) }
    }
}
