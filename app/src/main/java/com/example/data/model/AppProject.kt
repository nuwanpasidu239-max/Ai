package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class AppProject(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "General",
    val prompt: String = "",
    val targetSdk: Long = 36L,
    val themeStyle: String = "Material 3 Dark Modern",
    val architecture: String = "MVVM + StateFlow + Clean Arch",
    val status: String = "completed",
    val codeFiles: Map<String, String> = emptyMap(),
    val previewComponentType: String = "general",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ChatMessage(
    @DocumentId val id: String = "",
    val userId: String = "",
    val projectId: String = "",
    val role: String = "user", // "user" or "assistant"
    val content: String = "",
    val codeSnippet: String = "",
    @ServerTimestamp val timestamp: Timestamp? = null
)

data class AppTemplate(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val prompt: String,
    val previewComponentType: String,
    val iconName: String,
    val initialFiles: Map<String, String>
)
