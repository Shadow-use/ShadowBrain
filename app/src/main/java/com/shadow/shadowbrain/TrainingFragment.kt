// Responsibility: UI для запуску автоматичного збору шрифтів та навчання
package com.shadow.shadowbrain

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment

class TrainingFragment : Fragment(R.layout.fragment_training) {
    private lateinit var uiController: UIController
    private lateinit var brainManager: BrainManager
    private val alphabet = ("А".."Я").toList().map { it.toString() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val status = view.findViewById<TextView>(R.id.statusText)
        val grid = view.findViewById<GridLayout>(R.id.gridInput)
        
        uiController = UIController(grid)
        brainManager = BrainManager(requireContext())
        
        // Вхід 256 (16x16), приховані шари 128, вихід 33 (алфавіт)
        brainManager.initBrain(intArrayOf(256, 128, 64, alphabet.size))

        // КНОПКА: ЗІБРАТИ ШРИФТИ (Harvest)
        view.findViewById<Button>(R.id.btnAddSample).apply {
            text = "ЗІБРАТИ ШРИФТИ"
            setOnClickListener {
                Thread {
                    brainManager.harvestFonts(alphabet) { msg ->
                        activity?.runOnUiThread { status.text = msg }
                    }
                    activity?.runOnUiThread { status.text = "Шрифти зібрано в Documents/ShadowBrain/dataset.txt" }
                }.start()
            }
        }

        // КНОПКА: ВЧИТИ
        view.findViewById<Button>(R.id.btnTrainBatch).setOnClickListener {
            status.text = "Навчання на 100 шрифтах..."
            Thread {
                brainManager.trainFull(500) { epoch ->
                    activity?.runOnUiThread { status.text = "Епоха: $epoch" }
                }
                activity?.runOnUiThread { status.text = "Навчання завершено!" }
            }.start()
        }

        // КНОПКА: ПЕРЕВІРИТИ
        view.findViewById<Button>(R.id.btnPredict).setOnClickListener {
            val output = brainManager.brain?.feedForward(uiController.getInput())?.last()
            output?.let {
                val idx = it.indices.maxByOrNull { i -> it[i] } ?: 0
                status.text = "Це буква: ${alphabet[idx]} (${(it[idx]*100).toInt()}%)"
            }
        }

        view.findViewById<Button>(R.id.btnClear).setOnClickListener { uiController.clear() }
    }
}
