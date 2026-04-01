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
