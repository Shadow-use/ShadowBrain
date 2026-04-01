// Responsibility: UI для керування навчанням та автоматичного збору датасету
package com.shadow.shadowbrain

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment

class TrainingFragment : Fragment(R.layout.fragment_training) {
    private lateinit var uiController: UIController
    private lateinit var brainManager: BrainManager
    
    // Явно створюємо список, щоб уникнути помилок Unresolved reference
    private val alphabet: List<String> = listOf(
        "А", "Б", "В", "Г", "Ґ", "Д", "Е", "Є", "Ж", "З", "И", "І", "Ї", "Й",
        "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч",
        "Ш", "Щ", "Ь", "Ю", "Я"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val status = view.findViewById<TextView>(R.id.statusText)
        val grid = view.findViewById<GridLayout>(R.id.gridInput)
        
        uiController = UIController(grid)
        brainManager = BrainManager(requireContext())
        
        // Вхід 256 (16x16), приховані шари 128, 64, вихід за розміром алфавіту
        brainManager.initBrain(intArrayOf(256, 128, 64, alphabet.size))

        // КНОПКА: ЗІБРАТИ ШРИФТИ (Harvest)
        view.findViewById<Button>(R.id.btnAddSample)?.apply {
            text = "ЗІБРАТИ ШРИФТИ"
            setOnClickListener {
                status.text = "Починаємо збір..."
                Thread {
                    try {
                        brainManager.harvestFonts(alphabet) { msg ->
                            activity?.runOnUiThread { status.text = msg }
                        }
                        activity?.runOnUiThread { 
                            status.text = "Готово! Файли в Documents/ShadowBrain/" 
                        }
                    } catch (e: Exception) {
                        activity?.runOnUiThread { status.text = "Помилка: ${e.message}" }
                    }
                }.start()
            }
        }

        // КНОПКА: ВЧИТИ
        view.findViewById<Button>(R.id.btnTrainBatch)?.setOnClickListener {
            status.text = "Навчання..."
            Thread {
                try {
                    brainManager.trainFull(500) { epoch ->
                        activity?.runOnUiThread { status.text = "Епоха: $epoch" }
                    }
                    activity?.runOnUiThread { status.text = "Навчання завершено успішно!" }
                } catch (e: Exception) {
                    activity?.runOnUiThread { status.text = "Помилка навчання: ${e.message}" }
                }
            }.start()
        }

        // КНОПКА: ПЕРЕВІРИТИ
        view.findViewById<Button>(R.id.btnPredict)?.setOnClickListener {
            val output = brainManager.brain?.feedForward(uiController.getInput())?.last()
            if (output != null) {
                var maxIdx = 0
                var maxVal = output[0]
                for (i in output.indices) {
                    if (output[i] > maxVal) {
                        maxVal = output[i]
                        maxIdx = i
                    }
                }
                val confidence = (maxVal * 100).toInt()
                status.text = "Результат: ${alphabet[maxIdx]} ($confidence%)"
            } else {
                status.text = "Мізки не ініціалізовані"
            }
        }

        // КНОПКА: ОЧИСТИТИ
        view.findViewById<Button>(R.id.btnClear)?.setOnClickListener {
            uiController.clear()
            status.text = "Сітку очищено"
        }
    }
}
