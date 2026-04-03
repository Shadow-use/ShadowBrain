// Responsibility: Build configuration with Lifecycle, Fragment KTX and Coroutine support
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.shadow.shadowbrain"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.shadow.shadowbrain"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.code.gson:gson:2.10.1")

    // ViewModel та LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    
    // Екстеншни для Фрагментів (потрібно для 'by viewModels()')
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    
    // Lifecycle Runtime (потрібно для lifecycleScope та repeatOnLifecycle)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
}
