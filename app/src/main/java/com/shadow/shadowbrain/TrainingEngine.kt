// Responsibility: Training loop on data streams
package com.shadow.shadowbrain

class TrainingEngine(
    private val brain: NeuralNetwork,
    private val dataManager: DatasetManager,
    private val storage: BrainStorage
) {
    @Volatile var shouldStop = false

    fun run(epochs: Int, onProgress: (Int, Int, Int) -> Unit) {
        shouldStop = false
        
        // Тут ми більше не вантажимо все в RAM, а просто отримуємо Sequence
        val datasetSequence = dataManager.streamDataset()
        val totalSamples = dataManager.streamDataset().count() // Можна кешувати розмір
        
        if (totalSamples == 0) return

        for (epoch in 1..epochs) {
            if (shouldStop) break
            
            // На жаль, Sequence важко Shuffle без завантаження в RAM. 
            // Компроміс: тренуємо як є, або вантажимо лише індекси.
            datasetSequence.forEachIndexed { i, sample ->
                if (shouldStop) return@forEachIndexed
                
                val (label, input) = sample
                val target = DoubleArray(brain.layerSizes.last()) { 0.0 }
                target[label] = 1.0
                
                brain.train(input, target)
                onProgress(epoch, i + 1, totalSamples)
            }
            storage.save(brain)
        }
    }
}
