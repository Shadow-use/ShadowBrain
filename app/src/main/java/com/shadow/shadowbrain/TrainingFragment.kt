// Responsibility: UI wiring for Progress reporting, Resetting, and Predicting
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

        val spinner = view.findViewById<Spinner>(R.id.labelSpinner)
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, alphabet)

        // 1. ADD / HARVEST
        view.findViewById<Button>(R.id.btnAddSample).apply {
            setOnClickListener {
                brainManager.saveSample(spinner.selectedItemPosition, ui.getInput())
                status.text = "Зразок [${alphabet[spinner.selectedItemPosition]}] додано"
                ui.clear()
            }
            setOnLongClickListener {
                status.text = "Збираю шрифти з архіву..."
                Thread {
                    brainManager.harvestFonts(alphabet) { msg ->
                        activity?.runOnUiThread { status.text = msg }
                    }
                    activity?.runOnUiThread { status.text = "Архів зібрано. База: ${brainManager.getDatasetSize()} записів" }
                }.start()
                true
            }
        }

        // 2. TRAIN / RESET BRAIN
        view.findViewById<Button>(R.id.btnTrainBatch).apply {
            setOnClickListener {
                Thread {
                    brainManager.trainFull(500) { ep, cur, total ->
                        activity?.runOnUiThread { status.text = "Епоха $ep/500 | Прогрес: $cur/$total" }
                    }
                    activity?.runOnUiThread { status.text = "Навчання завершено!" }
                }.start()
            }
            setOnLongClickListener {
                brainManager.resetBrain()
                brainManager.initBrain(alphabet.size)
                status.text = "Мізки очищено (JSON видалено)"
                true
            }
        }

        // 3. PREDICT (Око нейронки)
        view.findViewById<Button>(R.id.btnPredict).setOnClickListener {
            val input = ui.getInput()
            brainManager.savePreview(input) // Зберігаємо картинку того, що бачить нейронка
            
            val out = brainManager.brain?.feedForward(input)?.last()
            out?.let {
                val idx = it.indices.maxByOrNull { i -> it[i] } ?: 0
                status.text = "Це буква: ${alphabet[idx]} (${(it[idx]*100).toInt()}%)"
            }
        }

        // 4. CLEAR GRID / CLEAR DATASET
        view.findViewById<Button>(R.id.btnClear).apply {
            setOnClickListener { ui.clear(); status.text = "Сітку очищено" }
            setOnLongClickListener {
                brainManager.clearDataset()
                status.text = "БАЗУ ДАНИХ ВИДАЛЕНО (TXT порожній)"
                true
            }
        }
    }
}
