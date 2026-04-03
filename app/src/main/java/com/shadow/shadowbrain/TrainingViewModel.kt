// Responsibility: Business Logic holder - survives rotation, throttles UI updates
package com.shadow.shadowbrain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrainingViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = BrainStorage(application)
    private val dataManager = DatasetManager(application, storage)
    
    private val _status = MutableStateFlow("ShadowBrain: Ready")
    val status = _status.asStateFlow()

    private var brain: NeuralNetwork = storage.load(intArrayOf(256, 128, 64, 33))
    private val engine = TrainingEngine(brain, dataManager, storage)

    private var lastUiUpdateTime = 0L

    fun train(epochs: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            engine.run(epochs) { ep, cur, total ->
                val now = System.currentTimeMillis()
                if (now - lastUiUpdateTime > 100 || cur == total) {
                    _status.value = "Epoch $ep | $cur/$total"
                    lastUiUpdateTime = now
                }
            }
            withContext(Dispatchers.Main) {
                _status.value = "Training Complete"
            }
        }
    }

    fun saveManualSample(labelIndex: Int, input: DoubleArray) {
        dataManager.saveSample(labelIndex, input)
        _status.value = "Зразок додано"
    }

    fun predict(input: DoubleArray): Pair<Int, Int> {
        dataManager.savePreview(input, "last_predict")
        val output = brain.feedForward(input).last()
        val maxIdx = output.indices.maxByOrNull { output[it] } ?: 0
        return maxIdx to (output[maxIdx] * 100).toInt()
    }

    fun harvest(alphabet: List<String>) {
        viewModelScope.launch(Dispatchers.Default) {
            dataManager.harvest(alphabet) { _status.value = it }
            _status.value = "Harvest Done"
        }
    }

    fun stop() { engine.shouldStop = true }

    fun resetBrain(alphabetSize: Int) {
        storage.delete()
        brain = storage.load(intArrayOf(256, 128, 64, alphabetSize))
        _status.value = "Модель скинуто"
    }
}
