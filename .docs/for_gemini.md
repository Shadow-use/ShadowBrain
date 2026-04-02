# PROJECT: ShadowBrain

## 0. AI INSTRUCTION
> Gemini, якщо ти не впевнений у поточній реалізації — НЕ ВИГАДУЙ. Запитай актуальний код: `NEED_FILE: [назва]`.

## 1. ABOUT & PLAN

​1. Оновлений План (Phase 5: Революція Даних)
​[x] Крок 1: Код додано (BrainManager, UI, Logger).
​[ ] Крок 2: Адаптація під Шрифти. Твоя мережа зараз чекає на вхід 9 нейронів (intArrayOf(9, ...)). Щоб "з'їсти" шрифти, нам треба або збільшити вхід до 256 (16x16), або навчити мережу на стиснутих образах.
​[ ] Крок 3: Dataset Manager. Потрібен модуль, який автоматично прожене всі .ttf з assets і збереже їх у твій dataset.txt як вектори.
## 2. STRUCTURE

```
.
├── app
│   ├── build.gradle.kts
│   ├── debug.keystore
│   └── src
│       └── main
│           ├── AndroidManifest.xml
│           ├── assets
│           │   └── fonts
│           │       ├── AGCrownStyle_Oblique.ttf
│           │       ├── Abetka_Kirnarskoho.ttf
│           │       ├── Adana_Script.ttf
│           │       ├── Adana_script_Deco.ttf
│           │       ├── AdverGothic_Ho.ttf
│           │       ├── AgitProp_Medium.ttf
│           │       ├── Alebarda.otf
│           │       ├── Alexandra_Zeferino_Three.ttf
│           │       ├── Alfavita.ttf
│           │       ├── Allegretto_Script_One_Regular.ttf
│           │       ├── AmazDooMLeft.ttf
│           │       ├── AmazDooMLeftOutline.ttf
│           │       ├── AmazDooMRightOutline.ttf
│           │       ├── Ancient_Kyiv.ttf
│           │       ├── Aniron_Bold.ttf
│           │       ├── Anna-Faustina_script.ttf
│           │       ├── Antikvarika.ttf
│           │       ├── Ariadna_script.ttf
│           │       ├── Arnold_BocklinC_Initials.ttf
│           │       ├── Artemis_Deco.ttf
│           │       ├── Artemon__Regular.ttf
│           │       ├── Asia2AS.ttf
│           │       ├── Asturia_script.ttf
│           │       ├── AuX_DotBitC_Xtra_Bold.ttf
│           │       ├── Baris_Cerin.ttf
│           │       ├── Barocco_Initial.ttf
│           │       ├── Baron_Munchausen.ttf
│           │       ├── Batik_Deco.ttf
│           │       ├── Belukha.ttf
│           │       ├── Blagovest.ttf
│           │       ├── Bulgaria_Moderna_Pro.otf
│           │       ├── Burlak.ttf
│           │       ├── CMU_Typewriter_Text_BoldItalic.otf
│           │       ├── Cansellarist.ttf
│           │       ├── Chronicle.ttf
│           │       ├── Country_Western_Open.ttf
│           │       ├── Country_Western_Script_Open.ttf
│           │       ├── Cynthia_Handwriting_Bold.ttf
│           │       ├── Cynthia_Handwriting_Bold_Italic.ttf
│           │       ├── CyrillicOld_Bold.ttf
│           │       ├── DS_Down_Cyr.ttf
│           │       ├── DS_UstavHand.ttf
│           │       ├── Def_Writer_BASE_Cyr.ttf
│           │       ├── Derby.ttf
│           │       ├── Disco-Grudge_Rounded.otf
│           │       ├── Ekaterina_Velikaya_One.ttf
│           │       ├── Evangelie.ttf
│           │       ├── Flow_Bold.otf
│           │       ├── Fords_Folly.ttf
│           │       ├── FuturisXShadowC.ttf
│           │       ├── FuturisXShadowCTT.ttf
│           │       ├── Glide_Sketch.otf
│           │       ├── Graffiti3CTT.ttf
│           │       ├── JAGODINA_PRAZNA_KOSA_Italic.ttf
│           │       ├── KaligrafC.ttf
│           │       ├── Larisa_script.ttf
│           │       ├── Lovely_Sofia_BG.ttf
│           │       ├── Maassslicer3D.ttf
│           │       ├── Majestic_X.ttf
│           │       ├── Markiz_de_Sad_script.ttf
│           │       ├── Mateur.ttf
│           │       ├── Mon_Amour_One.ttf
│           │       ├── Mon_Amour_Two.ttf
│           │       ├── Njallur.TTF
│           │       ├── Nowy_Geroy_4F_Shadow_Italic.otf
│           │       ├── Nowy_Geroy_4F_Shadow_Regular.otf
│           │       ├── Olietta_script-Poesia_BoldItalic.ttf
│           │       ├── Olietta_script_Lyrica_BoldItalic.ttf
│           │       ├── Pancetta_Serif_Pro_Italic.otf
│           │       ├── Paneuropa_Bankette_Regular.ttf
│           │       ├── Paneuropa_Crash_barrier_Black.ttf
│           │       ├── Pero.ttf
│           │       ├── Polo_Brush_MF.ttf
│           │       ├── PresentScript.ttf
│           │       ├── Quimbie_Shaddow.ttf
│           │       ├── Redinger.ttf
│           │       ├── Regina_Kursiv.ttf
│           │       ├── Ribbon_script.ttf
│           │       ├── RodchenkoInlineC.ttf
│           │       ├── Rosamunda_Two_Regular.ttf
│           │       ├── Round_Script_Italic.ttf
│           │       ├── Teddy_Bear.ttf
│           │       ├── Tkachenko_Sketch_4F.ttf
│           │       ├── Tusch_Touch_4.ttf
│           │       ├── Valencia_script_One.ttf
│           │       ├── Valencia_script_Three.ttf
│           │       ├── Valencia_script_Two.ttf
│           │       ├── Venski_Sad_Two_Medium.ttf
│           │       ├── Vienna_Poster_Deco.ttf
│           │       ├── Vivaldi_script.ttf
│           │       ├── XAyax_Schatten.ttf
│           │       ├── Yakutovych.ttf
│           │       ├── Yiggivoo_Unicode_3D_Italic.ttf
│           │       ├── Zapf_Chance_Italic.ttf
│           │       ├── Zhizn.otf
│           │       ├── a_AlbionicB&W.ttf
│           │       ├── a_AlbionicTitulNrSh.ttf
│           │       ├── a_AlgeriusOtl.ttf
│           │       ├── pragmaticashadowctt_bolditalic.ttf
│           │       └── Пелагій.ttf
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

15 directories, 115 files
```

## 3. LOGIC

### Logical Map (Auto-generated)
- BrainManager.kt: Dataset management with a Visual Debug Gallery for all fonts
- UIController.kt: 16x16 Grid with Toggle Drawing (Draw/Erase)
- TrainingFragment.kt: UI with "Train 5 Epochs" and "STOP" functionality
- ShadowLogger.kt: Глобальне перехоплення та запис критичних помилок у файл
- MainActivity.kt: Тільки ініціалізація та запуск першого екрану
- NeuralNetwork.kt: Ядро нейронної мережі з підтримкою динамічних шарів та навчання

## 4. ACTUAL CODE (REQUESTED)


### 📂 FILE: ./app/src/main/java/com/shadow/shadowbrain/BrainManager.kt
```
// Responsibility: Dataset management with a Visual Debug Gallery for all fonts
package com.shadow.shadowbrain

import android.content.Context
import android.graphics.*
import android.os.Environment
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream

class BrainManager(private val context: Context) {
    private val gson = Gson()
    private val baseDir: File by lazy {
        context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
    }
    
    // Нова папка для візуальної перевірки
    private val samplesDir get() = File(baseDir, "samples").apply { if (!exists()) mkdirs() }
    private val dataFile get() = File(baseDir, "dataset.txt")
    private val brainFile get() = File(baseDir, "shadow_brain.json")
    
    var brain: NeuralNetwork? = null
    private val INPUT_RES = 16 
    @Volatile var shouldStop = false

    fun initBrain(alphabetSize: Int) {
        val layers = intArrayOf(INPUT_RES * INPUT_RES, 128, 64, alphabetSize)
        brain = if (brainFile.exists()) {
            try { gson.fromJson(brainFile.readText(), NeuralNetwork::class.java) } 
            catch (e: Exception) { NeuralNetwork(layers) }
        } else NeuralNetwork(layers)
    }

    // Зберігає конкретний зразок як PNG для галереї
    fun saveToGallery(input: DoubleArray, fileName: String) {
        val bitmap = Bitmap.createBitmap(INPUT_RES, INPUT_RES, Bitmap.Config.ARGB_8888)
        for (i in input.indices) {
            val color = if (input[i] == 1.0) Color.WHITE else Color.BLACK
            bitmap.setPixel(i % INPUT_RES, i / INPUT_RES, color)
        }
        val scaled = Bitmap.createScaledBitmap(bitmap, 128, 128, false)
        val file = File(samplesDir, "$fileName.png")
        FileOutputStream(file).use { scaled.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    fun harvestFonts(alphabet: List<String>, onProgress: (String) -> Unit) {
        val fontFiles = context.assets.list("fonts") ?: return
        dataFile.writeText("") 
        
        // Очищуємо стару галерею перед новим збором
        samplesDir.deleteRecursively()
        samplesDir.mkdirs()

        fontFiles.forEachIndexed { fIdx, fontName ->
            onProgress("Шрифт $fIdx: $fontName")
            val tf = try {
                Typeface.createFromAsset(context.assets, "fonts/$fontName")
            } catch (e: Exception) { null } ?: return@forEachIndexed

            alphabet.forEachIndexed { aIdx, char ->
                val vector = renderCharToVector(char, tf)
                saveSample(aIdx, vector)
                
                // Зберігаємо першу букву кожного шрифту для перевірки в галереї
                if (aIdx == 0) {
                    saveToGallery(vector, "font_${fIdx}_${char}")
                }
            }
        }
    }

    private fun renderCharToVector(char: String, tf: Typeface): DoubleArray {
        val bitmap = Bitmap.createBitmap(INPUT_RES, INPUT_RES, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            typeface = tf
            textSize = INPUT_RES * 0.85f
            textAlign = Paint.Align.CENTER
            color = Color.WHITE
        }
        // Трохи піднімаємо текст, щоб ніжки букв (як у Ф чи Ц) не вилітали за край
        canvas.drawText(char, INPUT_RES/2f, INPUT_RES*0.8f, paint)
        
        val vector = DoubleArray(INPUT_RES * INPUT_RES)
        for (i in 0 until INPUT_RES * INPUT_RES) {
            val pixel = bitmap.getPixel(i % INPUT_RES, i / INPUT_RES)
            vector[i] = if (Color.alpha(pixel) > 120) 1.0 else 0.0
        }
        return vector
    }

    fun trainStep(epochs: Int, onProgress: (Int, Int, Int) -> Unit) {
        shouldStop = false
        val lines = if (dataFile.exists()) dataFile.readLines().filter { it.contains("|") } else return
        if (lines.isEmpty()) return

        for (epoch in 1..epochs) {
            if (shouldStop) break
            val shuffled = lines.shuffled()
            shuffled.forEachIndexed { i, line ->
                if (shouldStop) return@forEachIndexed
                val parts = line.split("|")
                val input = parts[1].split(",").map { it.toDouble() }.toDoubleArray()
                val target = DoubleArray(brain?.layerSizes?.last() ?: 33) { 0.0 }
                target[parts[0].toInt()] = 1.0
                brain?.train(input, target)
                if (i % 100 == 0) onProgress(epoch, i, shuffled.size)
            }
            saveBrain()
        }
    }

    fun saveSample(labelIndex: Int, input: DoubleArray) {
        dataFile.appendText("$labelIndex|${input.joinToString(",")}\n")
    }

    fun saveBrain() {
        brain?.let { brainFile.writeText(gson.toJson(it)) }
    }
}
```

### 📂 FILE: ./app/src/main/java/com/shadow/shadowbrain/MainActivity.kt
```
// Responsibility: Тільки ініціалізація та запуск першого екрану
package com.shadow.shadowbrain

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ShadowLogger(this) // Стартуємо самописець
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TrainingFragment())
                .commit()
        }
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

### 📂 FILE: ./app/src/main/java/com/shadow/shadowbrain/ShadowLogger.kt
```
// Responsibility: Глобальне перехоплення та запис критичних помилок у файл
package com.shadow.shadowbrain

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ShadowLogger(val context: Context) {
    private val logFile = File(context.filesDir, "critical_errors.log")

    init {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            logError(throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    fun logError(e: Throwable) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val logEntry = """
            --- CRITICAL ERROR ---
            Time: $timestamp
            Message: ${e.message}
            Stacktrace: 
            ${e.stackTraceToString()}
            -----------------------
            
        """.trimIndent()
        
        logFile.appendText(logEntry)
    }
}
```

### 📂 FILE: ./app/src/main/java/com/shadow/shadowbrain/TrainingFragment.kt
```
// Responsibility: UI with "Train 5 Epochs" and "STOP" functionality
package com.shadow.shadowbrain

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment

class TrainingFragment : Fragment(R.layout.fragment_training) {
    private lateinit var ui: UIController
    private lateinit var brainManager: BrainManager
    private val alphabet = listOf("А", "Б", "В", "Г", "Ґ", "Д", "Е", "Є", "Ж", "З", "И", "І", "Ї", "Й", "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Ь", "Ю", "Я")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val status = view.findViewById<TextView>(R.id.statusText)
        brainManager = BrainManager(requireContext())
        brainManager.initBrain(alphabet.size)
        ui = UIController(view.findViewById(R.id.gridInput))

        // КНОПКА: ВЧИТИ 5 ЕПОХ
        view.findViewById<Button>(R.id.btnTrainBatch).apply {
            text = "ВЧИТИ 5 ЕПОХ"
            setOnClickListener {
                status.text = "Запуск 5 епох..."
                Thread {
                    brainManager.trainStep(5) { ep, cur, total ->
                        activity?.runOnUiThread { 
                            status.text = "Епоха $ep/5 | Зразок: $cur/$total" 
                        }
                    }
                    activity?.runOnUiThread { status.text = "5 епох пройдено. Мізки збережено." }
                }.start()
            }
            // Довгий клік — повне очищення мізків
            setOnLongClickListener {
                brainManager.resetBrain()
                brainManager.initBrain(alphabet.size)
                status.text = "Мізки видалено (JSON)"
                true
            }
        }

        // КНОПКА: СТОП (Використовуємо кнопку Predict або Clear як Stop під час навчання)
        view.findViewById<Button>(R.id.btnClear).apply {
            text = "СТОП / CLEAR"
            setOnClickListener {
                brainManager.shouldStop = true
                ui.clear()
                status.text = "ЗУПИНКА..."
            }
        }

        // РЕШТА КНОПОК (Add Sample, Predict)
        view.findViewById<Button>(R.id.btnAddSample).setOnClickListener {
            val spinner = view.findViewById<Spinner>(R.id.labelSpinner)
            brainManager.saveSample(spinner.selectedItemPosition, ui.getInput())
            status.text = "Зразок додано"
            ui.clear()
        }

        view.findViewById<Button>(R.id.btnPredict).setOnClickListener {
            val out = brainManager.brain?.feedForward(ui.getInput())?.last()
            out?.let {
                val idx = it.indices.maxByOrNull { i -> it[i] } ?: 0
                status.text = "Результат: ${alphabet[idx]} (${(it[idx]*100).toInt()}%)"
            }
        }
    }
}
```

### 📂 FILE: ./app/src/main/java/com/shadow/shadowbrain/UIController.kt
```
// Responsibility: 16x16 Grid with Toggle Drawing (Draw/Erase)
package com.shadow.shadowbrain

import android.graphics.Color
import android.widget.Button
import android.widget.GridLayout

class UIController(private val grid: GridLayout) {
    private val size = 16
    private val cells = DoubleArray(size * size) { 0.0 }
    private val buttons = mutableListOf<Button>()

    init {
        grid.removeAllViews()
        grid.columnCount = size
        val ctx = grid.context
        val displayWidth = ctx.resources.displayMetrics.widthPixels
        val btnSize = (displayWidth - 100) / size

        for (i in 0 until size * size) {
            val btn = Button(ctx).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = btnSize
                    height = btnSize
                }
                setPadding(0, 0, 0, 0)
                setBackgroundColor(Color.DKGRAY)
                setOnClickListener { 
                    if (cells[i] == 0.0) {
                        cells[i] = 1.0
                        setBackgroundColor(Color.CYAN)
                    } else {
                        cells[i] = 0.0
                        setBackgroundColor(Color.DKGRAY)
                    }
                }
            }
            buttons.add(btn)
            grid.addView(btn)
        }
    }

    fun getInput() = cells.copyOf()
    fun clear() {
        cells.fill(0.0)
        buttons.forEach { it.setBackgroundColor(Color.DKGRAY) }
    }
}
```
