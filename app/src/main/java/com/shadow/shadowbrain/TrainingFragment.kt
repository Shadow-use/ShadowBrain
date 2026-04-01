// Responsibility: Обробка UI навчання, збір даних з сітки та виклик методів нейромережі
package com.shadow.shadowbrain

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment

class TrainingFragment : Fragment(R.layout.fragment_training) {
    private lateinit var uiController: UIController
    private lateinit var brainManager: BrainManager
    private val letters = listOf("А", "Б", "В", "Г", "Д", "Е", "Є") // Для тесту

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val grid = view.findViewById<GridLayout>(R.id.gridInput)
        val statusText = view.findViewById<TextView>(R.id.statusText)
        val spinner = view.findViewById<Spinner>(R.id.labelSpinner)
        
        uiController = UIController(grid)
        brainManager = BrainManager(requireContext())
        brainManager.initBrain(intArrayOf(9, 16, letters.size))

        // Налаштування списку букв
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, letters)

        // Кнопка ВЧИТИ
        view.findViewById<Button>(R.id.btnTrain).setOnClickListener {
            val input = uiController.getInput()
            val target = DoubleArray(letters.size) { 0.0 }
            target[spinner.selectedItemPosition] = 1.0
            
            // Навчаємо 100 ітерацій за одне натискання
            repeat(100) { brainManager.brain?.train(input, target) }
            brainManager.saveBrain()
            statusText.text = "Навчено для: ${spinner.selectedItem}"
        }

        // Кнопка ВГАДАТИ
        view.findViewById<Button>(R.id.btnPredict).setOnClickListener {
            val input = uiController.getInput()
            val result = brainManager.brain?.feedForward(input)?.last() ?: return@setOnClickListener
            
            val bestMatchIndex = result.indices.maxByOrNull { result[it] } ?: -1
            val confidence = result[bestMatchIndex] * 100
            
            statusText.text = "Результат: ${letters[bestMatchIndex]} (${String.format("%.1f", confidence)}%)"
        }
    }
}
