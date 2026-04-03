package com.shadow.shadowbrain

import android.content.Context
import android.graphics.*
import java.io.*

class DatasetManager(private val context: Context, private val baseDir: File) {
    private val binFile = File(baseDir, "dataset.bin")
    private val INPUT_RES = 16
    private val RECORD_SIZE = 4 + (256 * 4) // Int (Label) + 256 Floats (Vector)

    // Записує кількість зразків у перші 4 байти файлу
    fun updateHeader(count: Int) {
        val raf = RandomAccessFile(binFile, "rw")
        raf.seek(0)
        raf.writeInt(count)
        raf.close()
    }

    fun getSamplesCount(): Int {
        if (!binFile.exists() || binFile.length() < 4) return 0
        val raf = RandomAccessFile(binFile, "r")
        val count = raf.readInt()
        raf.close()
        return count
    }

    // Читання конкретного зразка без завантаження всього файлу
    fun readSample(index: Int): Pair<Int, DoubleArray> {
        val raf = RandomAccessFile(binFile, "r")
        // Пропускаємо заголовок (4 байти) та попередні записи
        raf.seek(4L + index.toLong() * RECORD_SIZE)
        
        val label = raf.readInt()
        val vector = DoubleArray(256) { raf.readFloat().toDouble() }
        raf.close()
        return label to vector
    }

    fun harvest(alphabet: List<String>, onProgress: (String) -> Unit) {
        if (binFile.exists()) binFile.delete()
        var total = 0
        
        // Резервуємо місце під заголовок
        val raf = RandomAccessFile(binFile, "rw")
        raf.writeInt(0) 

        val fontFiles = context.assets.list("fonts") ?: return
        fontFiles.forEach { fontName ->
            onProgress("Harvest: $fontName")
            val tf = try { Typeface.createFromAsset(context.assets, "fonts/$fontName") } catch (e: Exception) { null } ?: return@forEach
            alphabet.forEachIndexed { idx, char ->
                val vector = renderToVector(char, tf)
                raf.writeInt(idx)
                vector.forEach { raf.writeFloat(it.toFloat()) }
                total++
            }
        }
        raf.seek(0)
        raf.writeInt(total)
        raf.close()
    }

    private fun renderToVector(char: String, tf: Typeface): DoubleArray {
        val bitmap = Bitmap.createBitmap(INPUT_RES, INPUT_RES, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply { typeface = tf; textSize = 13f; textAlign = Paint.Align.CENTER; color = Color.WHITE }
        canvas.drawText(char, 8f, 13f, paint)
        return DoubleArray(256) { i -> if (Color.alpha(bitmap.getPixel(i % 16, i / 16)) > 120) 1.0 else 0.0 }
    }
}
