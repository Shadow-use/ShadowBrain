// Responsibility: Dataset management with a Visual Debug Gallery for all fonts
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
    
    // Нова папка для візуальної перевірки
    private val samplesDir get() = File(baseDir, "samples").apply { if (!exists()) mkdirs() }
    private val dataFile get() = File(baseDir, "dataset.txt")
    private val brainFile get() = File(baseDir, "shadow_brain.json")
    
    var brain: NeuralNetwork? = null
    private val INPUT_RES = 16 
    @Volatile var shouldStop = false

    fun initBrain(alphabetSize: Int) {
        val layers = intArrayOf(INPUT_RES * INPUT_RES, 128, 64, alphabetSize)
        brain = if (brainFile.exists()) {
            try { gson.fromJson(brainFile.readText(), NeuralNetwork::class.java) } 
            catch (e: Exception) { NeuralNetwork(layers) }
        } else NeuralNetwork(layers)
    }

    // Зберігає конкретний зразок як PNG для галереї
    fun saveToGallery(input: DoubleArray, fileName: String) {
        val bitmap = Bitmap.createBitmap(INPUT_RES, INPUT_RES, Bitmap.Config.ARGB_8888)
        for (i in input.indices) {
            val color = if (input[i] == 1.0) Color.WHITE else Color.BLACK
            bitmap.setPixel(i % INPUT_RES, i / INPUT_RES, color)
        }
        val scaled = Bitmap.createScaledBitmap(bitmap, 128, 128, false)
        val file = File(samplesDir, "$fileName.png")
        FileOutputStream(file).use { scaled.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    fun harvestFonts(alphabet: List<String>, onProgress: (String) -> Unit) {
        val fontFiles = context.assets.list("fonts") ?: return
        dataFile.writeText("") 
        
        // Очищуємо стару галерею перед новим збором
        samplesDir.deleteRecursively()
        samplesDir.mkdirs()

        fontFiles.forEachIndexed { fIdx, fontName ->
            onProgress("Шрифт $fIdx: $fontName")
            val tf = try {
                Typeface.createFromAsset(context.assets, "fonts/$fontName")
            } catch (e: Exception) { null } ?: return@forEachIndexed

            alphabet.forEachIndexed { aIdx, char ->
                val vector = renderCharToVector(char, tf)
                saveSample(aIdx, vector)
                
                // Зберігаємо першу букву кожного шрифту для перевірки в галереї
                if (aIdx == 0) {
                    saveToGallery(vector, "font_${fIdx}_${char}")
                }
            }
        }
    }

    private fun renderCharToVector(char: String, tf: Typeface): DoubleArray {
        val bitmap = Bitmap.createBitmap(INPUT_RES, INPUT_RES, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            typeface = tf
            textSize = INPUT_RES * 0.85f
            textAlign = Paint.Align.CENTER
            color = Color.WHITE
        }
        // Трохи піднімаємо текст, щоб ніжки букв (як у Ф чи Ц) не вилітали за край
        canvas.drawText(char, INPUT_RES/2f, INPUT_RES*0.8f, paint)
        
        val vector = DoubleArray(INPUT_RES * INPUT_RES)
        for (i in 0 until INPUT_RES * INPUT_RES) {
            val pixel = bitmap.getPixel(i % INPUT_RES, i / INPUT_RES)
            vector[i] = if (Color.alpha(pixel) > 120) 1.0 else 0.0
        }
        return vector
    }

    fun trainStep(epochs: Int, onProgress: (Int, Int, Int) -> Unit) {
        shouldStop = false
        val lines = if (dataFile.exists()) dataFile.readLines().filter { it.contains("|") } else return
        if (lines.isEmpty()) return

        for (epoch in 1..epochs) {
            if (shouldStop) break
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
            saveBrain()
        }
    }

    fun saveSample(labelIndex: Int, input: DoubleArray) {
        dataFile.appendText("$labelIndex|${input.joinToString(",")}\n")
    }

    fun saveBrain() {
        brain?.let { brainFile.writeText(gson.toJson(it)) }
    }
}
