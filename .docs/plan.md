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