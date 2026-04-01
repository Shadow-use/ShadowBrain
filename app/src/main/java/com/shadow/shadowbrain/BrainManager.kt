// Responsibility: Робота з базою зразків та масове навчання (Batch Training)
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
                gson.fromJson(brainFile.readText(), NeuralNetwork::class.java)
            } catch (e: Exception) { NeuralNetwork(layers) }
        } else {
            NeuralNetwork(layers)
        }
    }

    // Зберігаємо малюнок у файл: "ІндексБукви|1,0,1,0,1,0,1,0,1"
    fun saveSample(labelIndex: Int, input: DoubleArray) {
        val line = "$labelIndex|${input.joinToString(",")}\n"
        dataFile.appendText(line)
    }

    // Масове навчання по всій базі
    fun trainFull(epochs: Int = 5000, onProgress: (Int) -> Unit) {
        if (!dataFile.exists()) return
        val lines = dataFile.readLines()
        if (lines.isEmpty()) return

        repeat(epochs) { epoch ->
            lines.forEach { line ->
                val parts = line.split("|")
                val labelIndex = parts[0].toInt()
                val input = parts[1].split(",").map { it.toDouble() }.toDoubleArray()
                
                val target = DoubleArray(brain?.layerSizes?.last() ?: 33) { 0.0 }
                target[labelIndex] = 1.0
                
                brain?.train(input, target)
            }
            if (epoch % 100 == 0) onProgress(epoch)
        }
        saveBrain()
    }

    fun saveBrain() {
        brain?.let { brainFile.writeText(gson.toJson(it)) }
    }
}
