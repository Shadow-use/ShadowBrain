// Responsibility: 16x16 Grid with Toggle Drawing (Draw/Erase)
package com.shadow.shadowbrain

import android.graphics.Color
import android.widget.Button
import android.widget.GridLayout

class UIController(private val grid: GridLayout) {
    private val size = 16
    private val cells = DoubleArray(size * size) { 0.0 }
    private val buttons = mutableListOf<Button>()

    init {
        grid.removeAllViews()
        grid.columnCount = size
        val ctx = grid.context
        val displayWidth = ctx.resources.displayMetrics.widthPixels
        val btnSize = (displayWidth - 100) / size

        for (i in 0 until size * size) {
            val btn = Button(ctx).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = btnSize
                    height = btnSize
                }
                setPadding(0, 0, 0, 0)
                setBackgroundColor(Color.DKGRAY)
                setOnClickListener { 
                    if (cells[i] == 0.0) {
                        cells[i] = 1.0
                        setBackgroundColor(Color.CYAN)
                    } else {
                        cells[i] = 0.0
                        setBackgroundColor(Color.DKGRAY)
                    }
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
