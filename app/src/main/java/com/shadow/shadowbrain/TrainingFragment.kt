// Responsibility: UI Orchestrator - coordinating ML logic via Coroutines and Custom View
package com.shadow.shadowbrain

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.shadow.shadowbrain.ui.PixelGridView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrainingFragment : Fragment(R.layout.fragment_training) {
    
    private lateinit var pixelGrid: PixelGridView
    private lateinit var storage: BrainStorage
    private lateinit var dataManager: DatasetManager
    private lateinit var engine: TrainingEngine
    private lateinit var brain: NeuralNetwork
    
    private var trainingJob: Job? = null

    private val alphabet = listOf(
        "А", "Б", "В", "Г", "Ґ", "Д", "Е", "Є", "Ж", "З", "И", "І", "Ї", "Й",
        "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч",
        "Ш", "Щ", "Ь", "Ю", "Я"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 1. Ініціалізація компонентів
        storage = BrainStorage(requireContext())
        dataManager = DatasetManager(requireContext(), storage)
        
        // Вхід 256 (16x16), приховані шари 128, 64, вихід 33
        brain = storage.load(intArrayOf(256, 128, 64, alphabet.size))
        engine = TrainingEngine(brain, dataManager, storage)
        
        pixelGrid = view.findViewById(R.id.gridInput) // Це наш новий PixelGridView
        val status = view.findViewById<TextView>(R.id.statusText)
        val spinner = view.findViewById<Spinner>(R.id.labelSpinner)
        
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, alphabet)

        // 2. КНОПКА: НАВЧАННЯ (5 ЕПОХ)
        view.findViewById<Button>(R.id.btnTrainBatch).apply {
            setOnClickListener {
                if (trainingJob?.isActive == true) return@setOnClickListener
                
                trainingJob = viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                    engine.run(5) { ep, cur, total ->
                        updateStatus(status, "Епоха $ep/5 | $cur/$total")
                    }
                    updateStatus(status, "Навчання завершено успішно")
                }
            }
            setOnLongClickListener {
                storage.delete()
                brain = storage.load(intArrayOf(256, 128, 64, alphabet.size))
                status.text = "Модель скинуто до нуля"
                true
            }
        }

        // 3. КНОПКА: СТОП / ОЧИСТИТИ
        view.findViewById<Button>(R.id.btnClear).apply {
            setOnClickListener {
                engine.shouldStop = true
                trainingJob?.cancel()
                pixelGrid.clear()
                status.text = "Зупинено. Сітку очищено."
            }
            setOnLongClickListener {
                dataManager.clear()
                status.text = "Датасет видалено повністю"
                true
            }
        }

        // 4. КНОПКА: ДОДАТИ / ЗІБРАТИ (Harvest)
        view.findViewById<Button>(R.id.btnAddSample).apply {
            setOnClickListener {
                val input = pixelGrid.getRawData()
                dataManager.saveSample(spinner.selectedItemPosition, input)
                status.text = "Зразок [${alphabet[spinner.selectedItemPosition]}] додано"
                pixelGrid.clear()
            }
            setOnLongClickListener {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                    updateStatus(status, "Збираю шрифти...")
                    dataManager.harvest(alphabet) { msg ->
                        updateStatus(status, msg)
                    }
                    updateStatus(status, "Harvest завершено!")
                }
                true
            }
        }

        // 5. КНОПКА: ПЕРЕДБАЧИТИ (Predict)
        view.findViewById<Button>(R.id.btnPredict).setOnClickListener {
            val input = pixelGrid.getRawData()
            // Зберігаємо preview для візуального контролю
            dataManager.savePreview(input, "last_predict") 
            
            val output = brain.feedForward(input).last()
            val maxIdx = output.indices.maxByOrNull { output[it] } ?: 0
            val confidence = (output[maxIdx] * 100).toInt()
            
            status.text = "Результат: ${alphabet[maxIdx]} ($confidence%)"
        }
    }

    // Хелпер для безпечного оновлення UI з корутин
    private suspend fun updateStatus(view: TextView, message: String) {
        withContext(Dispatchers.Main) {
            view.text = message
        }
    }
}
