// Responsibility: Стійке читання бази та автоматична генерація датасету зі шрифтів
package com.shadow.shadowbrain

import android.content.Context
import android.graphics.*
import android.os.Environment
import com.google.gson.Gson
import java.io.File

class BrainManager(private val context: Context) {
    private val gson = Gson()
    
    // Зберігаємо у публічній папці Documents/ShadowBrain, щоб ти бачив файли
    private val baseDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ShadowBrain").apply { mkdirs() }
    private val brainFile = File(baseDir, "shadow_brain.json")
    private val dataFile = File(baseDir, "dataset.txt")
    
    var brain: NeuralNetwork? = null
    private val INPUT_SIZE = 16 // Сітка 16x16 = 256 входів

    fun initBrain(layers: IntArray) {
        brain = if (brainFile.exists()) {
            try {
                gson.fromJson(brainFile.readText(), NeuralNetwork::class.java)
            } catch (e: Exception) { NeuralNetwork(layers) }
        } else {
            NeuralNetwork(layers)
        }
    }

    // Автоматичний збір даних з усіх шрифтів у assets
    fun harvestFonts(alphabet: List<String>, onProgress: (String) -> Unit) {
        val fontFiles = context.assets.list("fonts") ?: return
        dataFile.writeText("") // Очищуємо старий датасет

        fontFiles.forEach { fontName ->
            onProgress("Обробка: $fontName")
            val typeface = Typeface.createFromAsset(context.assets, "fonts/$fontName")
            alphabet.forEachIndexed { index, char ->
                val vector = renderCharToVector(char, typeface)
                saveSample(index, vector)
            }
        }
    }

    private fun renderCharToVector(char: String, tf: Typeface): DoubleArray {
        val bitmap = Bitmap.createBitmap(INPUT_SIZE, INPUT_SIZE, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            typeface = tf
            textSize = INPUT_SIZE * 0.9f
            textAlign = Paint.Align.CENTER
            isAntiAlias = false // Для чітких пікселів
        }
        canvas.drawText(char, INPUT_SIZE / 2f, INPUT_SIZE * 0.8f, paint)

        val vector = DoubleArray(INPUT_SIZE * INPUT_SIZE)
        for (y in 0 until INPUT_SIZE) {
            for (x in 0 until INPUT_SIZE) {
                vector[y * INPUT_SIZE + x] = if (Color.alpha(bitmap.getPixel(x, y)) > 120) 1.0 else 0.0
            }
        }
        return vector
    }

    fun saveSample(labelIndex: Int, input: DoubleArray) {
        dataFile.appendText("$labelIndex|${input.joinToString(",")}\n")
    }

    fun trainFull(epochs: Int = 1000, onProgress: (Int) -> Unit) {
        if (!dataFile.exists()) return
        val lines = dataFile.readLines().filter { it.contains("|") }
        
        repeat(epochs) { epoch ->
            lines.shuffled().forEach { line ->
                val parts = line.split("|")
                val label = parts[0].toInt()
                val input = parts[1].split(",").map { it.toDouble() }.toDoubleArray()
                
                val target = DoubleArray(brain?.layerSizes?.last() ?: 33) { 0.0 }
                if (label < target.size) target[label] = 1.0
                brain?.train(input, target)
            }
            if (epoch % 10 == 0) onProgress(epoch)
        }
        saveBrain()
    }

    fun saveBrain() {
        brain?.let { brainFile.writeText(gson.toJson(it)) }
    }
}
