// Responsibility: UI для збору бази даних та запуску масового навчання
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
        uiController = UIController(view.findViewById(R.id.gridInput))
        
        brainManager = BrainManager(requireContext())
        brainManager.initBrain(intArrayOf(9, 24, alphabet.size)) // Збільшив Hidden Layer до 24

        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, alphabet)

        // Кнопка: Зберегти зразок (додати в базу)
        view.findViewById<Button>(R.id.btnTrain).apply {
            text = "ДОДАТИ ЗРАЗОК"
            setOnClickListener {
                brainManager.saveSample(spinner.selectedItemPosition, uiController.getInput())
                status.text = "Зразок [${spinner.selectedItem}] додано в базу"
                uiController.clear()
            }
        }

        // Кнопка: Масове навчання (Train All)
        view.findViewById<Button>(R.id.btnPredict).apply {
            text = "ВЧИТИ ВСЕ (BATCH)"
            setOnClickListener {
                status.text = "Навчання... зачекай"
                // В ідеалі це треба в Coroutine, але для тесту так:
                brainManager.trainFull { e -> status.text = "Епоха: $e" }
                status.text = "Мережу навчено на всій базі!"
            }
        }
    }
}
