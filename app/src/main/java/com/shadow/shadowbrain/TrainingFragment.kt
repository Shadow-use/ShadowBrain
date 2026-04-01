// Responsibility: Обробка всіх 4-х кнопок керування нейромережею та логіка навчання
package com.shadow.shadowbrain

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment

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
        brainManager.initBrain(intArrayOf(9, 24, alphabet.size))

        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, alphabet)

        // 1. ДОДАТИ ЗРАЗОК
        view.findViewById<Button>(R.id.btnAddSample).setOnClickListener {
            brainManager.saveSample(spinner.selectedItemPosition, uiController.getInput())
            status.text = "Збережено для [${spinner.selectedItem}]"
            uiController.clear()
        }

        // 2. ВЧИТИ ВСЕ (Batch Training)
        view.findViewById<Button>(R.id.btnTrainBatch).setOnClickListener {
            status.text = "Йде навчання (5000 епох)..."
            brainManager.trainFull { epoch -> 
                if (epoch % 500 == 0) {
                    activity?.runOnUiThread { status.text = "Епоха: $epoch" }
                }
            }
            status.text = "Навчання завершено успішно!"
        }

        // 3. ПЕРЕВІРИТИ (Predict)
        view.findViewById<Button>(R.id.btnPredict).setOnClickListener {
            val result = brainManager.brain?.feedForward(uiController.getInput())?.last()
            result?.let {
                val index = it.indices.maxByOrNull { i -> it[i] } ?: 0
                val confidence = it[index] * 100
                status.text = "Це [${alphabet[index]}] (${String.format("%.1f", confidence)}%)"
            }
        }

        // 4. ОЧИСТИТИ
        view.findViewById<Button>(R.id.btnClear).setOnClickListener {
            uiController.clear()
            status.text = "Сітку очищено"
        }
    }
}
