// Responsibility: UI with "Train 5 Epochs" and "STOP" functionality
package com.shadow.shadowbrain

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment

class TrainingFragment : Fragment(R.layout.fragment_training) {
    private lateinit var ui: UIController
    private lateinit var brainManager: BrainManager
    private val alphabet = listOf("А", "Б", "В", "Г", "Ґ", "Д", "Е", "Є", "Ж", "З", "И", "І", "Ї", "Й", "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Ь", "Ю", "Я")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val status = view.findViewById<TextView>(R.id.statusText)
        brainManager = BrainManager(requireContext())
        brainManager.initBrain(alphabet.size)
        ui = UIController(view.findViewById(R.id.gridInput))

        // КНОПКА: ВЧИТИ 5 ЕПОХ
        view.findViewById<Button>(R.id.btnTrainBatch).apply {
            text = "ВЧИТИ 5 ЕПОХ"
            setOnClickListener {
                status.text = "Запуск 5 епох..."
                Thread {
                    brainManager.trainStep(5) { ep, cur, total ->
                        activity?.runOnUiThread { 
                            status.text = "Епоха $ep/5 | Зразок: $cur/$total" 
                        }
                    }
                    activity?.runOnUiThread { status.text = "5 епох пройдено. Мізки збережено." }
                }.start()
            }
            // Довгий клік — повне очищення мізків
            setOnLongClickListener {
                brainManager.resetBrain()
                brainManager.initBrain(alphabet.size)
                status.text = "Мізки видалено (JSON)"
                true
            }
        }

        // КНОПКА: СТОП (Використовуємо кнопку Predict або Clear як Stop під час навчання)
        view.findViewById<Button>(R.id.btnClear).apply {
            text = "СТОП / CLEAR"
            setOnClickListener {
                brainManager.shouldStop = true
                ui.clear()
                status.text = "ЗУПИНКА..."
            }
        }

        // РЕШТА КНОПОК (Add Sample, Predict)
        view.findViewById<Button>(R.id.btnAddSample).setOnClickListener {
            val spinner = view.findViewById<Spinner>(R.id.labelSpinner)
            brainManager.saveSample(spinner.selectedItemPosition, ui.getInput())
            status.text = "Зразок додано"
            ui.clear()
        }

        view.findViewById<Button>(R.id.btnPredict).setOnClickListener {
            val out = brainManager.brain?.feedForward(ui.getInput())?.last()
            out?.let {
                val idx = it.indices.maxByOrNull { i -> it[i] } ?: 0
                status.text = "Результат: ${alphabet[idx]} (${(it[idx]*100).toInt()}%)"
            }
        }
    }
}
