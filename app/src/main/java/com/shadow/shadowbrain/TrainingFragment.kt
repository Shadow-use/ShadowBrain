// Responsibility: Orchestrator - linking UI events to specialized atomic managers
package com.shadow.shadowbrain

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment

class TrainingFragment : Fragment(R.layout.fragment_training) {
    private lateinit var gridManager: UIController
    private lateinit var storage: BrainStorage
    private lateinit var dataManager: DatasetManager
    private lateinit var engine: TrainingEngine
    private lateinit var brain: NeuralNetwork

    private val alphabet = listOf("А", "Б", "В", "Г", "Ґ", "Д", "Е", "Є", "Ж", "З", "И", "І", "Ї", "Й", "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Ь", "Ю", "Я")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Init Atomic Components
        storage = BrainStorage(requireContext())
        dataManager = DatasetManager(requireContext(), storage)
        brain = storage.load(intArrayOf(256, 128, 64, alphabet.size))
        engine = TrainingEngine(brain, dataManager, storage)
        gridManager = UIController(view.findViewById(R.id.gridInput))

        val status = view.findViewById<TextView>(R.id.statusText)
        val spinner = view.findViewById<Spinner>(R.id.labelSpinner)
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, alphabet)

        // Train 5 Epochs
        view.findViewById<Button>(R.id.btnTrainBatch).apply {
            setOnClickListener {
                Thread {
                    engine.run(5) { ep, cur, total ->
                        activity?.runOnUiThread { status.text = "Епоха $ep/5 | $cur/$total" }
                    }
                    activity?.runOnUiThread { status.text = "Навчання завершено" }
                }.start()
            }
            setOnLongClickListener {
                storage.delete()
                brain = storage.load(intArrayOf(256, 128, 64, alphabet.size))
                status.text = "Мізки видалено"
                true
            }
        }

        // Stop & Clear
        view.findViewById<Button>(R.id.btnClear).apply {
            setOnClickListener { engine.shouldStop = true; gridManager.clear(); status.text = "Стоп" }
            setOnLongClickListener { dataManager.clear(); status.text = "Базу очищено"; true }
        }

        // Add & Harvest
        view.findViewById<Button>(R.id.btnAddSample).apply {
            setOnClickListener {
                dataManager.saveSample(spinner.selectedItemPosition, gridManager.getInput())
                status.text = "Додано: ${alphabet[spinner.selectedItemPosition]}"
            }
            setOnLongClickListener {
                Thread {
                    dataManager.harvest(alphabet) { msg -> activity?.runOnUiThread { status.text = msg } }
                    activity?.runOnUiThread { status.text = "Збір завершено" }
                }.start()
                true
            }
        }

        // Predict
        view.findViewById<Button>(R.id.btnPredict).setOnClickListener {
            val out = brain.feedForward(gridManager.getInput()).last()
            val idx = out.indices.maxByOrNull { out[it] } ?: 0
            status.text = "Результат: ${alphabet[idx]} (${(out[idx]*100).toInt()}%)"
        }
    }
}
