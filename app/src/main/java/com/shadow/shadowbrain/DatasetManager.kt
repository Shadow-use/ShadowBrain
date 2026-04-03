// Responsibility: Binary Streaming I/O via Sequences (RAM efficient)
package com.shadow.shadowbrain

import android.content.Context
import android.graphics.*
import java.io.*

class DatasetManager(private val context: Context, private val storage: BrainStorage) {
    private val binFile get() = File(storage.baseDir, "dataset.bin")
    private val INPUT_RES = 16

    // Стрімінгове читання: повертає Sequence, який читає файл по мірі потреби
    fun streamDataset(): Sequence<Pair<Int, DoubleArray>> = sequence {
        if (!binFile.exists()) return@sequence
        
        DataInputStream(BufferedInputStream(FileInputStream(binFile))).use { dis ->
            try {
                while (dis.available() > 0) {
                    val label = dis.readInt()
                    val vector = DoubleArray(256) { dis.readFloat().toDouble() }
                    yield(label to vector)
                }
            } catch (e: Exception) { /* Log error or handle EOF */ }
        }
    }

    fun saveSample(labelIndex: Int, input: DoubleArray) {
        DataOutputStream(BufferedOutputStream(FileOutputStream(binFile, true))).use { dos ->
            dos.writeInt(labelIndex)
            input.forEach { dos.writeFloat(it.toFloat()) }
        }
    }

    fun harvest(alphabet: List<String>, onProgress: (String) -> Unit) {
        val fontFiles = context.assets.list("fonts") ?: return
        binFile.delete()
        fontFiles.forEach { fontName ->
            onProgress("Harvest: $fontName")
            val tf = try { Typeface.createFromAsset(context.assets, "fonts/$fontName") } catch (e: Exception) { null } ?: return@forEach
            alphabet.forEachIndexed { idx, char -> saveSample(idx, renderToVector(char, tf)) }
        }
    }

    private fun renderToVector(char: String, tf: Typeface): DoubleArray {
        val bitmap = Bitmap.createBitmap(INPUT_RES, INPUT_RES, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply { typeface = tf; textSize = 13f; textAlign = Paint.Align.CENTER; color = Color.WHITE }
        canvas.drawText(char, 8f, 13f, paint)
        return DoubleArray(256) { i -> if (Color.alpha(bitmap.getPixel(i % 16, i / 16)) > 120) 1.0 else 0.0 }
    }
    
    fun savePreview(input: DoubleArray, name: String) { /* ... існуюча реалізація ... */ }
    fun clear() { binFile.delete() }
}
