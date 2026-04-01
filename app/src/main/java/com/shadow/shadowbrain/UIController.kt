// Responsibility: Керування станом кнопок сітки 3x3
package com.shadow.shadowbrain

import android.graphics.Color
import android.widget.Button
import android.widget.GridLayout

class UIController(private val grid: GridLayout) {
    private val cells = DoubleArray(9) { 0.0 }
    private val buttons = mutableListOf<Button>()

    init {
        val ctx = grid.context
        for (i in 0 until 9) {
            val btn = Button(ctx).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    columnSpec = GridLayout.spec(i % 3, 1f)
                    rowSpec = GridLayout.spec(i / 3, 1f)
                }
                setBackgroundColor(Color.DKGRAY)
                setOnClickListener { 
                    cells[i] = if (cells[i] == 0.0) 1.0 else 0.0
                    this.setBackgroundColor(if (cells[i] == 1.0) Color.CYAN else Color.DKGRAY)
                }
            }
            buttons.add(btn)
            grid.addView(btn)
        }
    }

    fun getInput() = cells.copyOf()
    fun clear() {
        cells.fill(0.0)
        buttons.forEach { it.setBackgroundColor(Color.DKGRAY) }
    }
}
