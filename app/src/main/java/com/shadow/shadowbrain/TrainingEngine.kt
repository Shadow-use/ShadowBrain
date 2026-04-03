package com.shadow.shadowbrain

class TrainingEngine(
    private val brain: NeuralNetwork,
    private val dataManager: DatasetManager,
    private val storage: BrainStorage
) {
    @Volatile var shouldStop = false

    fun run(epochs: Int, onProgress: (Int, Int, Int) -> Unit) {
        shouldStop = false
        val count = dataManager.getSamplesCount()
        if (count == 0) return

        val indices = (0 until count).toMutableList()

        for (epoch in 1..epochs) {
            if (shouldStop) break
            indices.shuffle() // Справжній Random Shuffle кожного разу

            indices.forEachIndexed { i, idx ->
                if (shouldStop) return@forEachIndexed
                
                val (label, input) = dataManager.readSample(idx)
                val target = DoubleArray(brain.layerSizes.last()) { 0.0 }
                target[label] = 1.0
                
                brain.train(input, target)
                if (i % 20 == 0) onProgress(epoch, i + 1, count)
            }
            // Зберігаємо тільки після закінчення епохи
            storage.save(brain)
        }
    }
}
