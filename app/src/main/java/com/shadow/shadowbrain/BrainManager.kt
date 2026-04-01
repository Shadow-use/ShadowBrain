// Responsibility: Atomic utility for saving datasets and models using app-specific external storage.
package com.shadow.shadowbrain

import android.content.Context
import android.graphics.*
import android.os.Environment
import com.google.gson.Gson
import java.io.File

class BrainManager(private val context: Context) {
    private val gson = Gson()
    
    // Використовуємо той самий підхід, що в forge: getExternalFilesDir
    // Це гарантує роботу без ручного підтвердження прав
    private val baseDir: File by lazy {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) 
            ?: context.filesDir
        if (!dir.exists()) dir.mkdirs()
        dir
    }

    private val brainFile get() = File(baseDir, "shadow_brain.json")
    private val dataFile get() = File(baseDir, "dataset.txt")
    
    var brain: NeuralNetwork? = null
    private val INPUT_SIZE = 16 

    fun initBrain(layers: IntArray) {
        // Створюємо папку при старті, якщо її чомусь немає
        if (!baseDir.exists()) baseDir.mkdirs()

        brain = if (brainFile.exists()) {
            try {
                gson.fromJson(brainFile.readText(), NeuralNetwork::class.java)
            } catch (e: Exception) { NeuralNetwork(layers) }
        } else {
            NeuralNetwork(layers)
        }
    }

    fun harvestFonts(alphabet: List<String>, onProgress: (String) -> Unit) {
        val fontFiles = context.assets.list("fonts") ?: return
        
        // Очищуємо старий файл перед новим збором
        dataFile.writeText("") 

        fontFiles.forEach { fontName ->
            onProgress("Шрифт: $fontName")
            try {
                val typeface = Typeface.createFromAsset(context.assets, "fonts/$fontName")
                alphabet.forEachIndexed { index, char ->
                    val vector = renderCharToVector(char, typeface)
                    saveSample(index, vector)
                }
            } catch (e: Exception) {
                // Пропускаємо, якщо шрифт битий
            }
        }
    }

    fun saveSample(labelIndex: Int, input: DoubleArray) {
        // Дописуємо в кінець файлу
        dataFile.appendText("$labelIndex|${input.joinToString(",")}\n")
    }

    private fun renderCharToVector(char: String, tf: Typeface): DoubleArray {
        val bitmap = Bitmap.createBitmap(INPUT_SIZE, INPUT_SIZE, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            typeface = tf
            textSize = INPUT_SIZE * 0.9f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(char, INPUT_SIZE / 2f, INPUT_SIZE * 0.8f, paint)

        val vector = DoubleArray(INPUT_SIZE * INPUT_SIZE)
        for (i in 0 until INPUT_SIZE * INPUT_SIZE) {
            val x = i % INPUT_SIZE
            val y = i / INPUT_SIZE
            val pixel = bitmap.getPixel(x, y)
            vector[i] = if (Color.alpha(pixel) > 120) 1.0 else 0.0
        }
        return vector
    }

    fun trainFull(epochs: Int = 1000, onProgress: (Int) -> Unit) {
        if (!dataFile.exists()) return
        val lines = dataFile.readLines().filter { it.contains("|") }
        
        repeat(epochs) { epoch ->
            lines.shuffled().forEach { line ->
                val parts = line.split("|")
                val input = parts[1].split(",").map { it.toDouble() }.toDoubleArray()
                val target = DoubleArray(brain?.layerSizes?.last() ?: 33) { 0.0 }
                target[parts[0].toInt()] = 1.0
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
