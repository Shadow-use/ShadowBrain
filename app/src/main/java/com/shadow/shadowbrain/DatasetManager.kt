// Responsibility: High-performance Binary I/O for ML datasets
package com.shadow.shadowbrain

import android.content.Context
import android.graphics.*
import java.io.*

class DatasetManager(private val context: Context, private val storage: BrainStorage) {
    private val binFile get() = File(storage.baseDir, "dataset.bin")
    private val INPUT_RES = 16

    fun savePreview(input: DoubleArray, name: String) {
        val bitmap = Bitmap.createBitmap(INPUT_RES, INPUT_RES, Bitmap.Config.ARGB_8888)
        for (i in input.indices) bitmap.setPixel(i % INPUT_RES, i / INPUT_RES, if (input[i] == 1.0) Color.WHITE else Color.BLACK)
        val file = File(storage.baseDir, "$name.png")
        FileOutputStream(file).use { Bitmap.createScaledBitmap(bitmap, 256, 256, false).compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    fun harvest(alphabet: List<String>, onProgress: (String) -> Unit) {
        val fontFiles = context.assets.list("fonts") ?: return
        binFile.delete()
        fontFiles.forEachIndexed { fIdx, fontName ->
            onProgress("Зір: $fontName")
            val tf = try { Typeface.createFromAsset(context.assets, "fonts/$fontName") } 
                     catch (e: Exception) { null } ?: return@forEachIndexed
            alphabet.forEachIndexed { aIdx, char ->
                saveSample(aIdx, renderToVector(char, tf))
            }
        }
    }

    fun saveSample(labelIndex: Int, input: DoubleArray) {
        DataOutputStream(BufferedOutputStream(FileOutputStream(binFile, true))).use { dos ->
            dos.writeInt(labelIndex)
            input.forEach { dos.writeFloat(it.toFloat()) }
        }
    }

    fun readDataset(): List<Pair<Int, DoubleArray>> {
        if (!binFile.exists()) return emptyList()
        val result = mutableListOf<Pair<Int, DoubleArray>>()
        try {
            DataInputStream(BufferedInputStream(FileInputStream(binFile))).use { dis ->
                while (dis.available() > 0) {
                    val label = dis.readInt()
                    val vector = DoubleArray(256) { dis.readFloat().toDouble() }
                    result.add(label to vector)
                }
            }
        } catch (e: Exception) { }
        return result
    }

    private fun renderToVector(char: String, tf: Typeface): DoubleArray {
        val bitmap = Bitmap.createBitmap(INPUT_RES, INPUT_RES, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply { typeface = tf; textSize = INPUT_RES * 0.8f; textAlign = Paint.Align.CENTER; color = Color.WHITE }
        canvas.drawText(char, INPUT_RES / 2f, INPUT_RES * 0.8f, paint)
        return DoubleArray(256) { i -> if (Color.alpha(bitmap.getPixel(i % 16, i / 16)) > 120) 1.0 else 0.0 }
    }

    fun clear() { binFile.delete() }
}
