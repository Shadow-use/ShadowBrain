// Responsibility: Font harvesting, PNG gallery generation, and dataset.txt I/O
package com.shadow.shadowbrain

import android.content.Context
import android.graphics.*
import java.io.File
import java.io.FileOutputStream

class DatasetManager(private val context: Context, private val storage: BrainStorage) {
    private val dataFile get() = File(storage.baseDir, "dataset.txt")
    private val samplesDir get() = File(storage.baseDir, "samples").apply { if (!exists()) mkdirs() }
    private val INPUT_RES = 16

    fun harvest(alphabet: List<String>, onProgress: (String) -> Unit) {
        val fontFiles = context.assets.list("fonts") ?: return
        dataFile.writeText("")
        samplesDir.deleteRecursively()
        samplesDir.mkdirs()

        fontFiles.forEachIndexed { fIdx, fontName ->
            onProgress("Обробка: $fontName")
            val tf = try { Typeface.createFromAsset(context.assets, "fonts/$fontName") } 
                     catch (e: Exception) { null } ?: return@forEachIndexed

            alphabet.forEachIndexed { aIdx, char ->
                val vector = renderToVector(char, tf)
                saveSample(aIdx, vector)
                if (aIdx == 0) savePreview(vector, "f${fIdx}_$char")
            }
        }
    }

    private fun renderToVector(char: String, tf: Typeface): DoubleArray {
        val bitmap = Bitmap.createBitmap(INPUT_RES, INPUT_RES, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            typeface = tf
            textSize = INPUT_RES * 0.8f
            textAlign = Paint.Align.CENTER
            color = Color.WHITE
        }
        canvas.drawText(char, INPUT_RES / 2f, INPUT_RES * 0.8f, paint)
        
        return DoubleArray(INPUT_RES * INPUT_RES) { i ->
            if (Color.alpha(bitmap.getPixel(i % INPUT_RES, i / INPUT_RES)) > 120) 1.0 else 0.0
        }
    }

    fun saveSample(labelIndex: Int, input: DoubleArray) {
        dataFile.appendText("$labelIndex|${input.joinToString(",")}\n")
    }

    fun readAllLines(): List<String> = if (dataFile.exists()) dataFile.readLines() else emptyList()

    fun clear() { if (dataFile.exists()) dataFile.delete() }

    private fun savePreview(input: DoubleArray, name: String) {
        val bitmap = Bitmap.createBitmap(INPUT_RES, INPUT_RES, Bitmap.Config.ARGB_8888)
        for (i in input.indices) bitmap.setPixel(i % INPUT_RES, i / INPUT_RES, if (input[i] == 1.0) Color.WHITE else Color.BLACK)
        val file = File(samplesDir, "$name.png")
        FileOutputStream(file).use { Bitmap.createScaledBitmap(bitmap, 128, 128, false).compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}
