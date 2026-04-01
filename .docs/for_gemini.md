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
- BrainManager.kt: Стійке читання бази та автоматична генерація датасету зі шрифтів
- UIController.kt: Керування сіткою 16x16 для ручного малювання та перевірки
- TrainingFragment.kt: UI для запуску автоматичного збору шрифтів та навчання
- ShadowLogger.kt: Глобальне перехоплення та запис критичних помилок у файл
- MainActivity.kt: Тільки ініціалізація та запуск першого екрану
- NeuralNetwork.kt: Ядро нейронної мережі з підтримкою динамічних шарів та навчання
