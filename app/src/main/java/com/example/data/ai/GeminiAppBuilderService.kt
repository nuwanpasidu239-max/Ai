package com.example.data.ai

import android.util.Log
import com.example.data.model.ChatMessage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

data class GeneratedAppResult(
    val appName: String,
    val tagline: String,
    val category: String,
    val architecture: String,
    val description: String,
    val codeFiles: Map<String, String>,
    val previewType: String // "counter", "todo", "cart", "weather", "profile", "general"
)

class GeminiAppBuilderService {

    // Lazily initialize generativeModel as per Firebase AI Logic guidelines
    private val generativeModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    suspend fun generateApp(
        prompt: String,
        category: String,
        targetLanguage: String = "English"
    ): GeneratedAppResult {
        return withTimeout(60_000L) {
            try {
                val systemPrompt = """
You are AppCraft AI, a world-class Senior Android Architect and Jetpack Compose expert.
The user wants to generate a complete Android App based on their request.
User Prompt: "$prompt"
Category: "$category"
Response Language: "$targetLanguage" (If Sinhala, provide explanations and descriptions in Sinhala, but keep Kotlin code, class names, and standard comments clean and idiomatic).

Generate:
1. APP_NAME: [Creative Android App Name]
2. TAGLINE: [Catchy 1-line summary]
3. DESCRIPTION: [3-4 sentence description of features]
4. ARCHITECTURE: [Architecture details e.g. MVVM + StateFlow + Repository + Clean Arch]
5. PREVIEW_TYPE: [One of: "todo", "counter", "cart", "weather", "profile", "general"]
6. FILE: MainScreen.kt
```kotlin
// Complete runnable Jetpack Compose UI with modern Material 3 styling
```
7. FILE: MainViewModel.kt
```kotlin
// Complete ViewModel with MutableStateFlow, UiState sealed interface, and business logic
```
8. FILE: AppModels.kt
```kotlin
// Clean Kotlin data classes and domain entities
```
9. FILE: AndroidManifest.xml
```xml
// Manifest declarations with proper permissions
```
10. FILE: build.gradle.kts
```kotlin
// Dependencies and Gradle config
```
""".trimIndent()

                val response = generativeModel.generateContent(systemPrompt)
                val responseText = response.text ?: ""
                parseResponse(prompt, category, responseText)
            } catch (e: CancellationException) {
                if (e is TimeoutCancellationException) {
                    Log.e("GeminiAppBuilder", "AI generation timed out after 60s", e)
                    throw Exception("AI request timed out. Please try again with a shorter prompt.")
                }
                throw e
            } catch (e: Exception) {
                Log.e("GeminiAppBuilder", "Generation failed", e)
                throw e
            }
        }
    }

    suspend fun chatWithArchitect(
        history: List<ChatMessage>,
        userMessage: String,
        language: String = "English"
    ): String {
        return withTimeout(50_000L) {
            try {
                val conversationPrompt = buildString {
                    appendLine("You are AppCraft AI, an expert Android Software Engineer helping the user design, refactor, and build Android Jetpack Compose applications.")
                    appendLine("Language preference: $language. (If user asks in Sinhala or preference is Sinhala, explain clearly in Sinhala while keeping code syntax in Kotlin).")
                    appendLine("Conversation context:")
                    history.takeLast(6).forEach { msg ->
                        appendLine("${msg.role.uppercase()}: ${msg.content}")
                    }
                    appendLine("USER: $userMessage")
                    appendLine("ASSISTANT:")
                }

                val response = generativeModel.generateContent(conversationPrompt)
                response.text ?: "Could not generate response. Please try again."
            } catch (e: CancellationException) {
                if (e is TimeoutCancellationException) {
                    throw Exception("Chat response timed out.")
                }
                throw e
            } catch (e: Exception) {
                Log.e("GeminiAppBuilder", "Chat failed", e)
                throw e
            }
        }
    }

    private fun parseResponse(originalPrompt: String, category: String, rawText: String): GeneratedAppResult {
        var appName = ""
        var tagline = ""
        var description = ""
        var architecture = "MVVM + StateFlow + Clean Arch"
        var previewType = "general"
        val codeFiles = mutableMapOf<String, String>()

        val lines = rawText.lines()
        var currentFileName = ""
        val currentFileContent = StringBuilder()
        var isInCodeBlock = false

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("APP_NAME:", ignoreCase = true)) {
                appName = trimmed.substringAfter(":").trim()
            } else if (trimmed.startsWith("TAGLINE:", ignoreCase = true)) {
                tagline = trimmed.substringAfter(":").trim()
            } else if (trimmed.startsWith("DESCRIPTION:", ignoreCase = true)) {
                description = trimmed.substringAfter(":").trim()
            } else if (trimmed.startsWith("ARCHITECTURE:", ignoreCase = true)) {
                architecture = trimmed.substringAfter(":").trim()
            } else if (trimmed.startsWith("PREVIEW_TYPE:", ignoreCase = true)) {
                previewType = trimmed.substringAfter(":").trim().lowercase().filter { it.isLetter() }
            } else if (trimmed.startsWith("FILE:", ignoreCase = true)) {
                if (currentFileName.isNotBlank() && currentFileContent.isNotEmpty()) {
                    codeFiles[currentFileName] = currentFileContent.toString().trim()
                }
                currentFileName = trimmed.substringAfter(":").trim()
                currentFileContent.clear()
            } else if (trimmed.startsWith("```")) {
                if (isInCodeBlock) {
                    isInCodeBlock = false
                } else {
                    isInCodeBlock = true
                }
            } else {
                if (isInCodeBlock || currentFileName.isNotBlank()) {
                    currentFileContent.appendLine(line)
                }
            }
        }

        if (currentFileName.isNotBlank() && currentFileContent.isNotEmpty()) {
            codeFiles[currentFileName] = currentFileContent.toString().trim()
        }

        // Fallbacks if formatting was loose
        if (appName.isBlank()) {
            val firstWords = originalPrompt.split(" ").take(3).joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
            appName = "$firstWords App"
        }
        if (tagline.isBlank()) {
            tagline = "AI-Crafted Jetpack Compose Android Application"
        }
        if (description.isBlank()) {
            description = originalPrompt
        }
        if (codeFiles.isEmpty()) {
            codeFiles["MainScreen.kt"] = rawText
        }

        val validPreviewTypes = setOf("todo", "counter", "cart", "weather", "profile", "general")
        if (previewType !in validPreviewTypes) {
            previewType = when {
                originalPrompt.contains("todo", ignoreCase = true) || originalPrompt.contains("task", ignoreCase = true) -> "todo"
                originalPrompt.contains("count", ignoreCase = true) || originalPrompt.contains("step", ignoreCase = true) || originalPrompt.contains("fit", ignoreCase = true) -> "counter"
                originalPrompt.contains("shop", ignoreCase = true) || originalPrompt.contains("cart", ignoreCase = true) || originalPrompt.contains("store", ignoreCase = true) -> "cart"
                originalPrompt.contains("weather", ignoreCase = true) || originalPrompt.contains("climate", ignoreCase = true) -> "weather"
                else -> "general"
            }
        }

        return GeneratedAppResult(
            appName = appName,
            tagline = tagline,
            category = category,
            architecture = architecture,
            description = description,
            codeFiles = codeFiles,
            previewType = previewType
        )
    }
}
