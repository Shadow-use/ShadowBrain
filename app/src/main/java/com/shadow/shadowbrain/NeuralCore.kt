// Responsibility: Updated math core with Xavier Init and ReLU
package com.shadow.shadowbrain

import java.io.Serializable
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.sqrt
import java.util.Random

class NeuralNetwork(val layerSizes: IntArray, var learningRate: Double = 0.01) : Serializable {
    val weights: MutableList<Array<DoubleArray>> = mutableListOf()
    val biases: MutableList<DoubleArray> = mutableListOf()

    init {
        val rand = Random()
        for (i in 0 until layerSizes.size - 1) {
            val rows = layerSizes[i + 1]
            val cols = layerSizes[i]
            
            // Xavier Initialization: sqrt(2 / (input + output))
            val stdDev = sqrt(2.0 / (cols + rows))
            weights.add(Array(rows) { DoubleArray(cols) { rand.nextGaussian() * stdDev } })
            biases.add(DoubleArray(rows) { 0.01 }) // Маленьке зміщення для ReLU
        }
    }

    private fun relu(x: Double) = max(0.01 * x, x) // Leaky ReLU
    private fun reluDerivative(x: Double) = if (x > 0) 1.0 else 0.01

    private fun sigmoid(x: Double) = 1.0 / (1.0 + exp(-x))
    private fun sigmoidDerivative(x: Double) = x * (1.0 - x)

    // ... (feedForward та train тепер мають використовувати relu для прихованих шарів)
}
