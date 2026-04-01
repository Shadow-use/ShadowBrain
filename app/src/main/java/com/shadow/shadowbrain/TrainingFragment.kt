// Responsibility: Повний інтерфейс з функціями очищення бази та перестворення мізків
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
        
        // Встановлюємо нову архітектуру
        brainManager.initBrain(intArrayOf(9, 32, 32, alphabet.size))

        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, alphabet)

        // 1. ДОДАТИ ЗРАЗОК
        view.findViewById<Button>(R.id.btnAddSample).setOnClickListener {
            brainManager.saveSample(spinner.selectedItemPosition, uiController.getInput())
            status.text = "Збережено: ${alphabet[spinner.selectedItemPosition]}"
            uiController.clear()
        }

        // 2. ВЧИТИ ВСЕ (Клік - вчити, Довгий клік - ВИДАЛИТИ МІЗКИ)
        view.findViewById<Button>(R.id.btnTrainBatch).apply {
            setOnClickListener {
                status.text = "Навчання..."
                Thread {
                    try {
                        brainManager.trainFull(7000) { epoch -> 
                            activity?.runOnUiThread { status.text = "Епоха: $epoch / 7000" }
                        }
                        activity?.runOnUiThread { status.text = "ГОТОВО! Мізки оновлено." }
                    } catch (e: Exception) {
                        activity?.runOnUiThread { status.text = "Помилка: ${e.message}" }
                    }
                }.start()
            }
            
            setOnLongClickListener {
                val brainFile = File(requireContext().filesDir, "shadow_brain.json")
                if (brainFile.delete()) {
                    status.text = "МІЗКИ ВИДАЛЕНО. Перезапусти додаток!"
                    brainManager.initBrain(intArrayOf(9, 32, 32, alphabet.size))
                }
                true
            }
        }

        // 3. ПЕРЕВІРИТИ
        view.findViewById<Button>(R.id.btnPredict).setOnClickListener {
            val result = brainManager.brain?.feedForward(uiController.getInput())?.last()
            result?.let {
                val index = it.indices.maxByOrNull { i -> it[i] } ?: 0
                val confidence = it[index] * 100
                status.text = "Результат: [${alphabet[index]}] (${String.format("%.1f", confidence)}%)"
            }
        }

        // 4. ОЧИСТИТИ (Клік - сітка, Довгий клік - СТАТУС ФАЙЛІВ)
        view.findViewById<Button>(R.id.btnClear).apply {
            setOnClickListener { uiController.clear(); status.text = "Сітку очищено" }
            setOnLongClickListener {
                val dataFile = File(requireContext().filesDir, "dataset.txt")
                val brainFile = File(requireContext().filesDir, "shadow_brain.json")
                val report = "База: ${if(dataFile.exists()) dataFile.readLines().size else 0} | Мізки: ${brainFile.length()/1024} KB"
                status.text = report
                true
            }
        }
    }
}
