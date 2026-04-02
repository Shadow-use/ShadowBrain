// Responsibility: Execution of the training loop with interruption control
package com.shadow.shadowbrain

class TrainingEngine(
    private val brain: NeuralNetwork,
    private val dataManager: DatasetManager,
    private val storage: BrainStorage
) {
    @Volatile var shouldStop = false

    fun run(epochs: Int, onProgress: (Int, Int, Int) -> Unit) {
        shouldStop = false
        val lines = dataManager.readAllLines().filter { it.contains("|") }
        if (lines.isEmpty()) return

        for (epoch in 1..epochs) {
            if (shouldStop) break
            val shuffled = lines.shuffled()
            shuffled.forEachIndexed { i, line ->
                if (shouldStop) return@forEachIndexed
                
                val parts = line.split("|")
                val input = parts[1].split(",").map { it.toDouble() }.toDoubleArray()
                val target = DoubleArray(brain.layerSizes.last()) { 0.0 }
                target[parts[0].toInt()] = 1.0
                
                brain.train(input, target)
                if (i % 50 == 0) onProgress(epoch, i, shuffled.size)
            }
            storage.save(brain) // Автозбереження кожну епоху
        }
    }
}
