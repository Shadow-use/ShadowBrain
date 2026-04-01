// Responsibility: Ядро нейронної мережі з підтримкою динамічних шарів та навчання
package com.shadow.shadowbrain

import java.io.Serializable
import kotlin.math.exp

class NeuralNetwork(
    val layerSizes: IntArray, // Наприклад: [9, 16, 33]
    var learningRate: Double = 0.1
) : Serializable {

    // Ваги: weights[шар][нейрон_наступний][нейрон_поточний]
    val weights: MutableList<Array<DoubleArray>> = mutableListOf()
    val biases: MutableList<DoubleArray> = mutableListOf()

    init {
        for (i in 0 until layerSizes.size - 1) {
            val rows = layerSizes[i + 1]
            val cols = layerSizes[i]
            weights.add(Array(rows) { DoubleArray(cols) { Math.random() * 2 - 1 } })
            biases.add(DoubleArray(rows) { Math.random() * 2 - 1 })
        }
    }

    private fun sigmoid(x: Double) = 1.0 / (1.0 + exp(-x))
    private fun sigmoidDerivative(x: Double) = x * (1.0 - x)

    // Прямий хід (Передбачення)
    fun feedForward(input: DoubleArray): List<DoubleArray> {
        val activations = mutableListOf(input)
        var current = input
        
        for (i in weights.indices) {
            val next = DoubleArray(weights[i].size)
            for (j in weights[i].indices) {
                var sum = biases[i][j]
                for (k in weights[i][j].indices) {
                    sum += current[k] * weights[i][j][k]
                }
                next[j] = sigmoid(sum)
            }
            current = next
            activations.add(current)
        }
        return activations
    }

    // Навчання (Backpropagation)
    fun train(input: DoubleArray, target: DoubleArray) {
        val activations = feedForward(input)
        var errors = DoubleArray(target.size) { i -> target[i] - activations.last()[i] }

        for (i in weights.size - 1 downTo 0) {
            val currentLayer = activations[i + 1]
            val prevLayer = activations[i]
            val nextErrors = DoubleArray(prevLayer.size)

            for (j in weights[i].indices) {
                val delta = errors[j] * sigmoidDerivative(currentLayer[j])
                for (k in weights[i][j].indices) {
                    nextErrors[k] += weights[i][j][k] * delta
                    weights[i][j][k] += learningRate * delta * prevLayer[k]
                }
                biases[i][j] += learningRate * delta
            }
            errors = nextErrors
        }
    }
}
