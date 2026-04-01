// Responsibility: Зв'язок логіки з оновленими ID з fragment_training.xml
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    
    val status = view.findViewById<TextView>(R.id.statusText)
    val spinner = view.findViewById<Spinner>(R.id.labelSpinner)
    val grid = view.findViewById<GridLayout>(R.id.gridInput)
    
    uiController = UIController(grid)
    brainManager = BrainManager(requireContext())
    brainManager.initBrain(intArrayOf(9, 24, alphabet.size))

    spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, alphabet)

    // 1. ДОДАТИ ЗРАЗОК (Був btnTrain, став btnAddSample)
    view.findViewById<Button>(R.id.btnAddSample).setOnClickListener {
        brainManager.saveSample(spinner.selectedItemPosition, uiController.getInput())
        status.text = "Збережено для [${spinner.selectedItem}]"
        uiController.clear()
    }

    // 2. ВЧИТИ ВСЕ (Batch Training)
    view.findViewById<Button>(R.id.btnTrainBatch).setOnClickListener {
        status.text = "Йде навчання (5000 епох)..."
        // Виконуємо масове навчання
        brainManager.trainFull { epoch -> 
            if (epoch % 500 == 0) status.text = "Епоха: $epoch"
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
