// Responsibility: Керування життєвим циклом нейромережі та збереженням даних
package com.shadow.shadowbrain

import android.content.Context
import com.google.gson.Gson
import java.io.File

class BrainManager(private val context: Context) {
    private val gson = Gson()
    private val fileName = "shadow_brain.json"
    
    var brain: NeuralNetwork? = null

    // Ініціалізація: або завантажуємо стару, або створюємо нову
    fun initBrain(layers: IntArray) {
        val file = File(context.filesDir, fileName)
        brain = if (file.exists()) {
            loadBrain()
        } else {
            NeuralNetwork(layers)
        }
    }

    fun saveBrain() {
        brain?.let {
            val json = gson.toJson(it)
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                output.write(json.toByteArray())
            }
        }
    }

    private fun loadBrain(): NeuralNetwork? {
        return try {
            val file = File(context.filesDir, fileName)
            val json = file.readText()
            gson.fromJson(json, NeuralNetwork::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Метод для навчання на всьому алфавіті
    fun trainOnDataset(dataset: Map<String, DoubleArray>) {
        // Проганяємо навчання N разів (Epochs)
        repeat(1000) {
            dataset.forEach { (label, input) ->
                // Тут потрібна логіка перетворення label у target array
                // Наприклад: "А" -> [1.0, 0.0, 0.0...]
            }
        }
        saveBrain()
    }
}
