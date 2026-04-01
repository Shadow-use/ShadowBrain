// Responsibility: Atomic training steps with interruption support and auto-save
package com.shadow.shadowbrain

import android.content.Context
import android.graphics.*
import android.os.Environment
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream

class BrainManager(private val context: Context) {
    private val gson = Gson()
    private val baseDir: File by lazy {
        context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
    }
    private val dataFile get() = File(baseDir, "dataset.txt")
    private val brainFile get() = File(baseDir, "shadow_brain.json")
    
    var brain: NeuralNetwork? = null
    private val INPUT_RES = 16 

    // Прапорець для зупинки навчання
    @Volatile var shouldStop = false

    fun initBrain(alphabetSize: Int) {
        val layers = intArrayOf(INPUT_RES * INPUT_RES, 128, 64, alphabetSize)
        brain = if (brainFile.exists()) {
            try { gson.fromJson(brainFile.readText(), NeuralNetwork::class.java) } 
            catch (e: Exception) { NeuralNetwork(layers) }
        } else NeuralNetwork(layers)
    }

    // Навчання фіксованої кількості епох з можливістю переривання
    fun trainStep(epochs: Int, onProgress: (Int, Int, Int) -> Unit) {
        shouldStop = false
        val lines = if (dataFile.exists()) dataFile.readLines().filter { it.contains("|") } else return
        if (lines.isEmpty()) return

        for (epoch in 1..epochs) {
            if (shouldStop) break // Миттєва зупинка
            
            val shuffled = lines.shuffled()
            shuffled.forEachIndexed { i, line ->
                if (shouldStop) return@forEachIndexed
                
                val parts = line.split("|")
                val input = parts[1].split(",").map { it.toDouble() }.toDoubleArray()
                val target = DoubleArray(brain?.layerSizes?.last() ?: 33) { 0.0 }
                target[parts[0].toInt()] = 1.0
                
                brain?.train(input, target)
                
                if (i % 100 == 0) onProgress(epoch, i, shuffled.size)
            }
            saveBrain() // Зберігаємо після кожної епохи для безпеки
        }
    }

    fun saveBrain() {
        brain?.let { brainFile.writeText(gson.toJson(it)) }
    }

    fun saveSample(labelIndex: Int, input: DoubleArray) {
        dataFile.appendText("$labelIndex|${input.joinToString(",")}\n")
    }

    fun resetBrain() {
        if (brainFile.exists()) brainFile.delete()
    }
}
