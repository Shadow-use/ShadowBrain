// Responsibility: Data management, Model persistence, and Detailed training progress
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

    fun initBrain(alphabetSize: Int) {
        if (!baseDir.exists()) baseDir.mkdirs()
        val layers = intArrayOf(INPUT_RES * INPUT_RES, 128, 64, alphabetSize)
        
        brain = if (brainFile.exists()) {
            try { gson.fromJson(brainFile.readText(), NeuralNetwork::class.java) } 
            catch (e: Exception) { NeuralNetwork(layers) }
        } else NeuralNetwork(layers)
    }

    // Очищення мізків (Видалення JSON)
    fun resetBrain() {
        if (brainFile.exists()) brainFile.delete()
    }

    // Очищення бази (Видалення TXT)
    fun clearDataset() {
        if (dataFile.exists()) dataFile.delete()
    }

    // Зберігає "Preview" того, що бачить нейронка
    fun savePreview(input: DoubleArray) {
        val bitmap = Bitmap.createBitmap(INPUT_RES, INPUT_RES, Bitmap.Config.ARGB_8888)
        for (i in input.indices) {
            val color = if (input[i] == 1.0) Color.WHITE else Color.BLACK
            bitmap.setPixel(i % INPUT_RES, i / INPUT_RES, color)
        }
        val previewFile = File(baseDir, "last_eye_view.png")
        FileOutputStream(previewFile).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    fun harvestFonts(alphabet: List<String>, onProgress: (String) -> Unit) {
        val fontFiles = context.assets.list("fonts") ?: return
        fontFiles.forEach { fontName ->
            onProgress("Зчитую шрифт: $fontName")
            val tf = Typeface.createFromAsset(context.assets, "fonts/$fontName")
            alphabet.forEachIndexed { idx, char ->
                val vector = renderCharToVector(char, tf)
                saveSample(idx, vector)
            }
        }
    }

    private fun renderCharToVector(char: String, tf: Typeface): DoubleArray {
        val bitmap = Bitmap.createBitmap(INPUT_RES, INPUT_RES, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            typeface = tf
            textSize = INPUT_RES * 0.8f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(char, INPUT_RES/2f, INPUT_RES*0.75f, paint)
        val vector = DoubleArray(INPUT_RES * INPUT_RES)
        for (i in 0 until INPUT_RES * INPUT_RES) {
            val pixel = bitmap.getPixel(i % INPUT_RES, i / INPUT_RES)
            vector[i] = if (Color.alpha(pixel) > 120) 1.0 else 0.0
        }
        return vector
    }

    fun saveSample(labelIndex: Int, input: DoubleArray) {
        dataFile.appendText("$labelIndex|${input.joinToString(",")}\n")
    }

    // Детальне навчання з прогресом по кожному зразку
    fun trainFull(epochs: Int, onProgress: (Int, Int, Int) -> Unit) {
        if (!dataFile.exists()) return
        val lines = dataFile.readLines().filter { it.contains("|") }
        if (lines.isEmpty()) return

        repeat(epochs) { epoch ->
            val shuffled = lines.shuffled()
            shuffled.forEachIndexed { sampleIdx, line ->
                val parts = line.split("|")
                val input = parts[1].split(",").map { it.toDouble() }.toDoubleArray()
                val target = DoubleArray(brain?.layerSizes?.last() ?: 33) { 0.0 }
                target[parts[0].toInt()] = 1.0
                brain?.train(input, target)
                
                // Оновлюємо статус кожні 50 зразків, щоб не перевантажувати UI потік
                if (sampleIdx % 50 == 0) onProgress(epoch + 1, sampleIdx + 1, shuffled.size)
            }
        }
        saveBrain()
    }

    fun saveBrain() = brain?.let { brainFile.writeText(gson.toJson(it)) }
    fun getDatasetSize(): Int = if (dataFile.exists()) dataFile.readLines().size else 0
}
