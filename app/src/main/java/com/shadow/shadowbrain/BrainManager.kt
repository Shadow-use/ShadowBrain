// Responsibility: Persistence layer - saving and loading NeuralNetwork as JSON
package com.shadow.shadowbrain

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import java.io.File

class BrainStorage(private val context: Context) {
    private val gson = Gson()
    
    val baseDir: File by lazy {
        context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
    }
    
    private val brainFile get() = File(baseDir, "shadow_brain.json")

    fun save(brain: NeuralNetwork) {
        brainFile.writeText(gson.toJson(brain))
    }

    fun load(layers: IntArray): NeuralNetwork {
        return if (brainFile.exists()) {
            try {
                gson.fromJson(brainFile.readText(), NeuralNetwork::class.java)
            } catch (e: Exception) { NeuralNetwork(layers) }
        } else {
            NeuralNetwork(layers)
        }
    }

    fun delete() {
        if (brainFile.exists()) brainFile.delete()
    }
}
