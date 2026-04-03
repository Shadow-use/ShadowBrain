// Responsibility: Neural Core with Leaky ReLU (hidden) and Softmax (output)
package com.shadow.shadowbrain

import java.io.Serializable
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.sqrt
import java.util.Random

class NeuralNetwork(
    val layerSizes: IntArray,
    var learningRate: Double = 0.01
) : Serializable {

    val weights: MutableList<Array<DoubleArray>> = mutableListOf()
    val biases: MutableList<DoubleArray> = mutableListOf()

    init {
        val rand = Random()
        for (i in 0 until layerSizes.size - 1) {
            val rows = layerSizes[i + 1]
            val cols = layerSizes[i]
            
            // Xavier/He Initialization для стабільного старту
            val stdDev = sqrt(2.0 / cols)
            weights.add(Array(rows) { DoubleArray(cols) { rand.nextGaussian() * stdDev } })
            biases.add(DoubleArray(rows) { 0.01 })
        }
    }

    // Leaky ReLU для прихованих шарів (захист від "вмирання" нейронів)
    private fun relu(x: Double) = max(0.01 * x, x)
    private fun reluDerivative(x: Double) = if (x > 0) 1.0 else 0.01

    // Softmax для вихідного шару (розподіл імовірностей)
    private fun softmax(input: DoubleArray): DoubleArray {
        val maxVal = input.maxOrNull() ?: 0.0
        val exps = DoubleArray(input.size) { i -> exp(input[i] - maxVal) } // Max-shift для стійкості
        val sum = exps.sum()
        return DoubleArray(input.size) { i -> exps[i] / sum }
    }

    fun feedForward(input: DoubleArray): List<DoubleArray> {
        val activations = mutableListOf(input)
        var current = input
        
        for (i in weights.indices) {
            val nextRaw = DoubleArray(weights[i].size)
            for (j in weights[i].indices) {
                var sum = biases[i][j]
                for (k in weights[i][j].indices) {
                    sum += current[k] * weights[i][j][k]
                }
                nextRaw[j] = sum
            }
            // Останній шар — Softmax, решта — ReLU
            current = if (i == weights.size - 1) softmax(nextRaw) else nextRaw.map { relu(it) }.toDoubleArray()
            activations.add(current)
        }
        return activations
    }

    fun train(input: DoubleArray, target: DoubleArray) {
        val activations = feedForward(input)
        val output = activations.last()
        
        // Помилка для Softmax + Cross-Entropy: просто (Output - Target)
        var errors = DoubleArray(target.size) { i -> output[i] - target[i] }

        for (i in weights.size - 1 downTo 0) {
            val currentLayer = activations[i + 1]
            val prevLayer = activations[i]
            val nextErrors = DoubleArray(prevLayer.size)

            for (j in weights[i].indices) {
                // Градієнт залежить від активації: на виході він уже в errors, 
                // для прихованих додаємо похідну ReLU
                val gradient = if (i == weights.size - 1) errors[j] else errors[j] * reluDerivative(currentLayer[j])
                
                for (k in weights[i][j].indices) {
                    nextErrors[k] += weights[i][j][k] * gradient
                    // Оновлення ваг (Gradient Descent)
                    weights[i][j][k] -= learningRate * gradient * prevLayer[k]
                }
                biases[i][j] -= learningRate * gradient
            }
            errors = nextErrors
        }
    }
}
