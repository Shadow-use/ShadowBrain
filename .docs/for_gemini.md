# PROJECT CONTEXT: ShadowBrain

## 1. ABOUT

# About ShadowBrain
А маніфест??
---
// Responsibility: Обробка UI навчання, збір даних з сітки та виклик методів нейромережі
package com.shadow.shadowbrain

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment

class TrainingFragment : Fragment(R.layout.fragment_training) {
    private lateinit var uiController: UIController
    private lateinit var brainManager: BrainManager
    private val letters = listOf("А", "Б", "В", "Г", "Д", "Е", "Є") // Для тесту

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val grid = view.findViewById<GridLayout>(R.id.gridInput)
        val statusText = view.findViewById<TextView>(R.id.statusText)
        val spinner = view.findViewById<Spinner>(R.id.labelSpinner)
        
        uiController = UIController(grid)
        brainManager = BrainManager(requireContext())
        brainManager.initBrain(intArrayOf(9, 16, letters.size))

        // Налаштування списку букв
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, letters)

        // Кнопка ВЧИТИ
        view.findViewById<Button>(R.id.btnTrain).setOnClickListener {
            val input = uiController.getInput()
            val target = DoubleArray(letters.size) { 0.0 }
            target[spinner.selectedItemPosition] = 1.0
            
            // Навчаємо 100 ітерацій за одне натискання
            repeat(100) { brainManager.brain?.train(input, target) }
            brainManager.saveBrain()
            statusText.text = "Навчено для: ${spinner.selectedItem}"
        }

        // Кнопка ВГАДАТИ
        view.findViewById<Button>(R.id.btnPredict).setOnClickListener {
            val input = uiController.getInput()
            val result = brainManager.brain?.feedForward(input)?.last() ?: return@setOnClickListener
            
            val bestMatchIndex = result.indices.maxByOrNull { result[it] } ?: -1
            val confidence = result[bestMatchIndex] * 100
            
            statusText.text = "Результат: ${letters[bestMatchIndex]} (${String.format("%.1f", confidence)}%)"
        }
    }
}
---activity_main.xml
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/container"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#121212" />
---fragment_training.xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    android:background="#121212">

    <TextView
        android:id="@+id/statusText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="ShadowBrain: Ready"
        android:textColor="#00FF00"
        android:textSize="18sp"
        android:layout_marginBottom="16dp" />

    <GridLayout
        android:id="@+id/gridInput"
        android:layout_width="240dp"
        android:layout_height="240dp"
        android:layout_gravity="center"
        android:columnCount="3"
        android:rowCount="3">
        </GridLayout>

    <Space
        android:layout_width="match_parent"
        android:layout_height="24dp" />

    <Spinner
        android:id="@+id/labelSpinner"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:background="#333333" />

    <Button
        android:id="@+id/btnTrain"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="ВЧИТИ (TRAIN)"
        android:backgroundTint="#2E7D32"
        android:layout_marginTop="16dp" />

    <Button
        android:id="@+id/btnPredict"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="ВГАДАТИ (PREDICT)"
        android:backgroundTint="#1565C0"
        android:layout_marginTop="8dp" />

</LinearLayout>

## 2. PLAN

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
## 3. STRUCTURE

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

## 4. LOGIC

### Logical Map (Auto-generated)
- BrainManager.kt: Керування життєвим циклом нейромережі та збереженням даних
- UIController.kt: Керування станом кнопок сітки 3x3
- TrainingFragment.kt: Обробка UI навчання, збір даних з сітки та виклик методів нейромережі
- ShadowLogger.kt: Глобальне перехоплення та запис критичних помилок у файл
- MainActivity.kt: Тільки ініціалізація та запуск першого екрану
- NeuralNetwork.kt: Ядро нейронної мережі з підтримкою динамічних шарів та навчання
