// Responsibility: Стійке до помилок читання бази та масове навчання
package com.shadow.shadowbrain

import android.content.Context
import com.google.gson.Gson
import java.io.File

class BrainManager(private val context: Context) {
    private val gson = Gson()
    private val brainFile = File(context.filesDir, "shadow_brain.json")
    private val dataFile = File(context.filesDir, "dataset.txt")
    
    var brain: NeuralNetwork? = null

    fun initBrain(layers: IntArray) {
        brain = if (brainFile.exists()) {
            try {
                val json = brainFile.readText()
                gson.fromJson(json, NeuralNetwork::class.java)
            } catch (e: Exception) { NeuralNetwork(layers) }
        } else {
            NeuralNetwork(layers)
        }
    }

    fun saveSample(labelIndex: Int, input: DoubleArray) {
        val line = "$labelIndex|${input.joinToString(",")}\n"
        dataFile.appendText(line)
    }

    fun trainFull(epochs: Int = 5000, onProgress: (Int) -> Unit) {
        if (!dataFile.exists()) return
        // Фільтруємо порожні рядки відразу
        val lines = dataFile.readLines().filter { it.contains("|") }
        if (lines.isEmpty()) return

        repeat(epochs) { epoch ->
            for (line in lines) {
                try {
                    val parts = line.split("|")
                    if (parts.size < 2) continue
                    
                    val labelIndex = parts[0].toInt()
                    val input = parts[1].split(",").map { it.toDouble() }.toDoubleArray()
                    
                    val outputSize = brain?.layerSizes?.last() ?: 33
                    val target = DoubleArray(outputSize) { 0.0 }
                    if (labelIndex in 0 until outputSize) {
                        target[labelIndex] = 1.0
                    }
                    
                    brain?.train(input, target)
                } catch (e: Exception) {
                    // Ігноруємо битий рядок
                    continue
                }
            }
            if (epoch % 100 == 0) onProgress(epoch)
        }
        saveBrain()
    }

    fun saveBrain() {
        try {
            brain?.let { brainFile.writeText(gson.toJson(it)) }
        } catch (e: Exception) {
            // Резервний лог, якщо запис не вдався
        }
    }
}
