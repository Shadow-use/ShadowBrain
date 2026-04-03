// Responsibility: UI Orchestrator - observing ViewModel state and handling user input
package com.shadow.shadowbrain

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.shadow.shadowbrain.ui.PixelGridView
import kotlinx.coroutines.launch

class TrainingFragment : Fragment(R.layout.fragment_training) {

    // ViewModel виживає при повороті екрана
    private val viewModel: TrainingViewModel by viewModels()
    private lateinit var pixelGrid: PixelGridView

    private val alphabet = listOf(
        "А", "Б", "В", "Г", "Ґ", "Д", "Е", "Є", "Ж", "З", "И", "І", "Ї", "Й",
        "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч",
        "Ш", "Щ", "Ь", "Ю", "Я"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        pixelGrid = view.findViewById(R.id.gridInput)
        val status = view.findViewById<TextView>(R.id.statusText)
        val spinner = view.findViewById<Spinner>(R.id.labelSpinner)
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, alphabet)

        // 1. БЕЗПЕЧНА ПІДПИСКА НА СТАТУС
        // repeatOnLifecycle гарантує, що ми не оновлюємо UI, коли додаток у фоні
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.status.collect { msg ->
                    status.text = msg
                }
            }
        }

        // 2. КНОПКА: НАВЧАННЯ (5 ЕПОХ)
        view.findViewById<Button>(R.id.btnTrainBatch).apply {
            setOnClickListener {
                viewModel.train(5)
            }
            setOnLongClickListener {
                viewModel.resetBrain(alphabet.size)
                true
            }
        }

        // 3. КНОПКА: ДОДАТИ / HARVEST (LONG)
        view.findViewById<Button>(R.id.btnAddSample).apply {
            setOnClickListener {
                // Додавання ручного зразка в датасет
                viewModel.saveManualSample(spinner.selectedItemPosition, pixelGrid.getRawData())
                pixelGrid.clear()
            }
            setOnLongClickListener {
                viewModel.harvest(alphabet)
                true
            }
        }

        // 4. КНОПКА: ПЕРЕДБАЧИТИ (Predict)
        view.findViewById<Button>(R.id.btnPredict).setOnClickListener {
            val (maxIdx, confidence) = viewModel.predict(pixelGrid.getRawData())
            status.text = "Результат: ${alphabet[maxIdx]} ($confidence%)"
        }

        // 5. КНОПКА: СТОП / ОЧИСТИТИ
        view.findViewById<Button>(R.id.btnClear).setOnClickListener {
            viewModel.stop()
            pixelGrid.clear()
        }
    }
}
