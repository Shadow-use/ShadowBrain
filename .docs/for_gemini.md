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
- BrainManager.kt: Керування життєвим циклом нейромережі та збереженням даних
- UIController.kt: Керування станом кнопок сітки 3x3
- TrainingFragment.kt: Обробка UI навчання, збір даних з сітки та виклик методів нейромережі
- ShadowLogger.kt: Глобальне перехоплення та запис критичних помилок у файл
- MainActivity.kt: Тільки ініціалізація та запуск першого екрану
- NeuralNetwork.kt: Ядро нейронної мережі з підтримкою динамічних шарів та навчання
