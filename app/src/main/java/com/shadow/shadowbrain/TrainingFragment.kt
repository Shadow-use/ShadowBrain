// Responsibility: Повний інтерфейс керування з дебаг-функціями для мобільного
package com.shadow.shadowbrain

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import java.io.File

class TrainingFragment : Fragment(R.layout.fragment_training) {
    private lateinit var uiController: UIController
    private lateinit var brainManager: BrainManager
    private val alphabet = listOf(
        "А", "Б", "В", "Г", "Ґ", "Д", "Е", "Є", "Ж", "З", "И", "І", "Ї", "Й", 
        "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", 
        "Ш", "Щ", "Ь", "Ю", "Я"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val status = view.findViewById<TextView>(R.id.statusText)
        val spinner = view.findViewById<Spinner>(R.id.labelSpinner)
        val grid = view.findViewById<GridLayout>(R.id.gridInput)
        
        uiController = UIController(grid)
        brainManager = BrainManager(requireContext())
        
        // Ініціалізація: 9 входів, два шари по 32 нейрони, 33 виходи
        brainManager.initBrain(intArrayOf(9, 32, 32, alphabet.size))

        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, alphabet)

        // 1. ДОДАТИ ЗРАЗОК
        view.findViewById<Button>(R.id.btnAddSample).setOnClickListener {
            brainManager.saveSample(spinner.selectedItemPosition, uiController.getInput())
            status.text = "Збережено для [${spinner.selectedItem}] (інд: ${spinner.selectedItemPosition})"
            uiController.clear()
        }

        // 2. ВЧИТИ ВСЕ (Batch Training)
        view.findViewById<Button>(R.id.btnTrainBatch).setOnClickListener {
            status.text = "Навчання розпочато..."
            Thread {
                try {
                    brainManager.trainFull(5000) { epoch -> 
                        activity?.runOnUiThread { status.text = "Епоха: $epoch / 5000" }
                    }
                    activity?.runOnUiThread { status.text = "Навчання ЗАВЕРШЕНО!" }
                } catch (e: Exception) {
                    activity?.runOnUiThread { status.text = "Помилка: ${e.message}" }
                }
            }.start()
        }

        // 3. ПЕРЕВІРИТИ (Predict)
        view.findViewById<Button>(R.id.btnPredict).setOnClickListener {
            val result = brainManager.brain?.feedForward(uiController.getInput())?.last()
            result?.let {
                val index = it.indices.maxByOrNull { i -> it[i] } ?: 0
                val confidence = it[index] * 100
                status.text = "Результат: [${alphabet[index]}] (${String.format("%.1f", confidence)}%)"
            }
        }

        // 4. ОЧИСТИТИ (Клік - очистка, Довгий клік - ДЕБАГ ФАЙЛІВ)
        view.findViewById<Button>(R.id.btnClear).apply {
            setOnClickListener {
                uiController.clear()
                status.text = "Сітку очищено"
            }
            
            setOnLongClickListener {
                val dataFile = File(requireContext().filesDir, "dataset.txt")
                val brainFile = File(requireContext().filesDir, "shadow_brain.json")
                
                val report = StringBuilder()
                if (dataFile.exists()) {
                    val lines = dataFile.readLines()
                    report.append("База: ${lines.size} зразків.\n")
                    if (lines.isNotEmpty()) report.append("Останній: ${lines.last().split("|")[0]}\n")
                } else report.append("Бази НЕМАЄ.\n")
                
                if (brainFile.exists()) {
                    val size = brainFile.length() / 1024
                    report.append("Мізки: $size KB.")
                } else report.append("Мізків НЕМАЄ.")
                
                status.text = report.toString()
                true
            }
        }
    }
}
