# PROJECT: ShadowBrain

## 0. AI INSTRUCTION
> Gemini, якщо ти не впевнений у поточній реалізації — НЕ ВИГАДУЙ. Запитай актуальний код: `NEED_FILE: [назва]`.

## 1. ABOUT & PLAN

## Current Plan
- [x] Phase 1: Init
- [x] Phase 2: Neural Core
- [x] Phase 3: Persistence & Logging
- [x] Phase 4: Training UI (Fragment & Grid)
- [ ] Phase 5: Training Optimization (Dataset manager)
- [ ] Phase 6: Export Module (AAR library)
Оновлений ПЛАН (Чіткий і технічний)
​Замість "робити щось", давай бити по цілях:
​Крок 1: Додати UIController.kt та ShadowLogger.kt (повністю).
​Крок 2: Зібрати проект (./gradlew assembleDebug). Якщо випадуть помилки — ShadowLogger їх не запише (бо це помилки компіляції), їх треба дивитися в консолі.
​Крок 3 (Перший запуск): Побачити сітку. Натиснути кнопки. Переконатися, що вони синіють.
​Крок 4 (Навчання): Намалювати "А", натиснути Train. Закрити додаток. Відкрити знову. Натиснути Predict. Якщо він скаже "А" — ShadowBrain народився.
## 2. STRUCTURE

```
.
├── app
│   ├── build.gradle.kts
│   ├── debug.keystore
│   └── src
│       └── main
│           ├── AndroidManifest.xml
│           ├── java
│           │   └── com
│           │       └── shadow
│           │           └── shadowbrain
│           │               ├── BrainManager.kt
│           │               ├── MainActivity.kt
│           │               ├── NeuralNetwork.kt
│           │               ├── ShadowLogger.kt
│           │               ├── TrainingFragment.kt
│           │               └── UIController.kt
│           └── res
│               ├── layout
│               │   ├── activity_main.xml
│               │   └── fragment_training.xml
│               └── values
├── build.gradle.kts
├── gradle
│   └── wrapper
│       └── gradle-wrapper.properties
├── gradle.properties
└── settings.gradle.kts

13 directories, 15 files
```

## 3. LOGIC

### Logical Map (Auto-generated)
- BrainManager.kt: Робота з базою зразків та масове навчання (Batch Training)
- UIController.kt: Керування станом кнопок сітки 3x3
- TrainingFragment.kt: Обробка всіх 4-х кнопок керування нейромережею та логіка навчання
- ShadowLogger.kt: Глобальне перехоплення та запис критичних помилок у файл
- MainActivity.kt: Тільки ініціалізація та запуск першого екрану
- NeuralNetwork.kt: Ядро нейронної мережі з підтримкою динамічних шарів та навчання

## 4. ACTUAL CODE (REQUESTED)


### 📂 FILE: ./app/src/main/java/com/shadow/shadowbrain/BrainManager.kt
```
// Responsibility: Робота з базою зразків та масове навчання (Batch Training)
package com.shadow.shadowbrain

import android.content.Context
import com.google.gson.Gson
import java.io.File

class BrainManager(private val context: Context) {
    private val gson = Gson()
    private val brainFile = File(context.filesDir, "shadow_brain.json")
    private val dataFile = File(context.filesDir, "dataset.txt")
    
    var brain: NeuralNetwork? = null

    fun initBrain(layers: IntArray) {
        brain = if (brainFile.exists()) {
            try {
                gson.fromJson(brainFile.readText(), NeuralNetwork::class.java)
            } catch (e: Exception) { NeuralNetwork(layers) }
        } else {
            NeuralNetwork(layers)
        }
    }

    // Зберігаємо малюнок у файл: "ІндексБукви|1,0,1,0,1,0,1,0,1"
    fun saveSample(labelIndex: Int, input: DoubleArray) {
        val line = "$labelIndex|${input.joinToString(",")}\n"
        dataFile.appendText(line)
    }

    // Масове навчання по всій базі
    fun trainFull(epochs: Int = 5000, onProgress: (Int) -> Unit) {
        if (!dataFile.exists()) return
        val lines = dataFile.readLines()
        if (lines.isEmpty()) return

        repeat(epochs) { epoch ->
            lines.forEach { line ->
                val parts = line.split("|")
                val labelIndex = parts[0].toInt()
                val input = parts[1].split(",").map { it.toDouble() }.toDoubleArray()
                
                val target = DoubleArray(brain?.layerSizes?.last() ?: 33) { 0.0 }
                target[labelIndex] = 1.0
                
                brain?.train(input, target)
            }
            if (epoch % 100 == 0) onProgress(epoch)
        }
        saveBrain()
    }

    fun saveBrain() {
        brain?.let { brainFile.writeText(gson.toJson(it)) }
    }
}
```

### 📂 FILE: ./app/src/main/java/com/shadow/shadowbrain/NeuralNetwork.kt
```
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
```
