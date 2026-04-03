package com.shadow.shadowbrain

import java.io.Serializable
import kotlin.math.*
import java.util.Random

class NeuralNetwork(val layerSizes: IntArray, var learningRate: Double = 0.01) : Serializable {
    val weights: MutableList<Array<DoubleArray>> = mutableListOf()
    val biases: MutableList<DoubleArray> = mutableListOf()

    init {
        val rand = Random()
        for (i in 0 until layerSizes.size - 1) {
            val stdDev = sqrt(2.0 / layerSizes[i])
            weights.add(Array(layerSizes[i + 1]) { DoubleArray(layerSizes[i]) { rand.nextGaussian() * stdDev } })
            biases.add(DoubleArray(layerSizes[i + 1]) { 0.01 })
        }
    }

    private fun relu(x: Double) = max(0.01 * x, x)
    private fun reluDerivative(x: Double) = if (x > 0) 1.0 else 0.01

    private fun softmax(input: DoubleArray): DoubleArray {
        val maxVal = input.maxOrNull() ?: 0.0
        val exps = DoubleArray(input.size) { i -> exp(input[i] - maxVal) }
        val sum = exps.sum()
        return DoubleArray(input.size) { i -> exps[i] / (if (sum == 0.0) 1.0 else sum) }
    }

    fun feedForward(input: DoubleArray): Pair<List<DoubleArray>, List<DoubleArray>> {
        val sums = mutableListOf<DoubleArray>() // z values
        val activations = mutableListOf(input) // a values
        var current = input

        for (i in weights.indices) {
            val z = DoubleArray(weights[i].size)
            for (j in weights[i].indices) {
                var sum = biases[i][j]
                for (k in weights[i][j].indices) sum += current[k] * weights[i][j][k]
                z[j] = sum
            }
            sums.add(z)
            current = if (i == weights.size - 1) softmax(z) else z.map { relu(it) }.toDoubleArray()
            activations.add(current)
        }
        return sums to activations
    }

    fun train(input: DoubleArray, target: DoubleArray) {
        val (sums, activations) = feedForward(input)
        val output = activations.last()
        
        // Для Softmax + Cross-Entropy похибка на виході: (a - y)
        var delta = DoubleArray(target.size) { i -> output[i] - target[i] }

        for (i in weights.size - 1 downTo 0) {
            val prevA = activations[i]
            val currentZ = sums[i]
            val nextDelta = DoubleArray(prevA.size)

            for (j in weights[i].indices) {
                // Використовуємо z для розрахунку похідної ReLU на прихованих шарах
                val d = if (i == weights.size - 1) delta[j] else delta[j] * reluDerivative(currentZ[j])
                
                for (k in weights[i][j].indices) {
                    nextDelta[k] += weights[i][j][k] * d
                    weights[i][j][k] -= learningRate * d * prevA[k]
                }
                biases[i][j] -= learningRate * d
            }
            delta = nextDelta
        }
    }
}
