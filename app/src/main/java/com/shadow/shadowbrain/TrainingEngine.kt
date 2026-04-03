// Responsibility: Training loop optimized for Binary data streaming
package com.shadow.shadowbrain

class TrainingEngine(
    private val brain: NeuralNetwork,
    private val dataManager: DatasetManager,
    private val storage: BrainStorage
) {
    @Volatile var shouldStop = false

    fun run(epochs: Int, onProgress: (Int, Int, Int) -> Unit) {
        shouldStop = false
        
        // Зчитуємо бінарні дані один раз у пам'ять
        val dataset = dataManager.readDataset()
        if (dataset.isEmpty()) return

        for (epoch in 1..epochs) {
            if (shouldStop) break
            
            // Перемішуємо для кращого навчання (Stochastic Gradient Descent)
            val shuffled = dataset.shuffled()
            
            shuffled.forEachIndexed { i, sample ->
                if (shouldStop) return@forEachIndexed
                
                val (label, input) = sample
                val target = DoubleArray(brain.layerSizes.last()) { 0.0 }
                target[label] = 1.0
                
                brain.train(input, target)
                if (i % 50 == 0) onProgress(epoch, i, shuffled.size)
            }
            storage.save(brain)
        }
    }
}
