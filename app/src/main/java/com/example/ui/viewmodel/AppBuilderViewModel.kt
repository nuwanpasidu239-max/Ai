package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiAppBuilderService
import com.example.data.ai.GeneratedAppResult
import com.example.data.model.AppProject
import com.example.data.model.AppTemplate
import com.example.data.model.ChatMessage
import com.example.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface GenerationState {
    data object Idle : GenerationState
    data class Generating(val step: String) : GenerationState
    data class Success(val result: GeneratedAppResult) : GenerationState
    data class Error(val message: String) : GenerationState
}

class AppBuilderViewModel(
    private val repository: ProjectRepository,
    private val currentUserId: String,
    private val aiService: GeminiAppBuilderService = GeminiAppBuilderService()
) : ViewModel() {

    // Language toggle: true = Sinhala, false = English
    private val _isSinhala = MutableStateFlow(true)
    val isSinhala = _isSinhala.asStateFlow()

    fun toggleLanguage() {
        _isSinhala.value = !_isSinhala.value
    }

    // Projects list stream from Firestore
    val projects: StateFlow<List<AppProject>> = repository.observeProjects(currentUserId)
        .catch { e ->
            Log.e("AppBuilderVM", "Error loading projects", e)
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _selectedProject = MutableStateFlow<AppProject?>(null)
    val selectedProject = _selectedProject.asStateFlow()

    private val _generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val generationState = _generationState.asStateFlow()

    // Chat messages for current project
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading = _isChatLoading.asStateFlow()

    // Interactive Playground State
    private val _playgroundCounter = MutableStateFlow(1250)
    val playgroundCounter = _playgroundCounter.asStateFlow()

    private val _playgroundTodos = MutableStateFlow(
        listOf(
            "Implement Material 3 Scaffold" to true,
            "Connect Firestore Snapshot Flow" to true,
            "Add Gemini 3.8 Flash AI Model" to true,
            "Build Interactive Compose Preview" to false
        )
    )
    val playgroundTodos = _playgroundTodos.asStateFlow()

    private val _playgroundCart = MutableStateFlow(
        listOf(
            "Pixel 9 Pro" to 999.0,
            "Cyber Earbuds" to 149.0
        )
    )
    val playgroundCart = _playgroundCart.asStateFlow()

    fun incrementCounter() {
        _playgroundCounter.value += 100
    }

    fun resetCounter() {
        _playgroundCounter.value = 0
    }

    fun toggleTodo(index: Int) {
        val current = _playgroundTodos.value.toMutableList()
        if (index in current.indices) {
            val (text, done) = current[index]
            current[index] = text to !done
            _playgroundTodos.value = current
        }
    }

    fun addTodo(title: String) {
        if (title.isNotBlank()) {
            _playgroundTodos.value = _playgroundTodos.value + (title to false)
        }
    }

    fun addToCart(name: String, price: Double) {
        _playgroundCart.value = _playgroundCart.value + (name to price)
    }

    fun clearCart() {
        _playgroundCart.value = emptyList()
    }

    fun selectProject(project: AppProject?) {
        _selectedProject.value = project
        if (project != null) {
            viewModelScope.launch {
                repository.observeMessages(currentUserId, project.id)
                    .catch { emit(emptyList()) }
                    .collect { msgs ->
                        _chatMessages.value = msgs
                    }
            }
        } else {
            _chatMessages.value = emptyList()
        }
    }

    fun generateAppWithAi(prompt: String, category: String) {
        if (prompt.isBlank()) return
        val lang = if (_isSinhala.value) "Sinhala" else "English"
        val stepMsg = if (_isSinhala.value) "AI මොඩලය මඟින් App එක නිර්මාණය කරමින් පවතී..." else "Architecting Android Jetpack Compose App..."
        _generationState.value = GenerationState.Generating(stepMsg)

        viewModelScope.launch {
            try {
                val result = aiService.generateApp(prompt, category, lang)
                _generationState.value = GenerationState.Success(result)

                // Persist new app project to Cloud Firestore
                val newProject = AppProject(
                    userId = currentUserId,
                    name = result.appName,
                    description = result.description,
                    category = result.category,
                    prompt = prompt,
                    targetSdk = 36L,
                    architecture = result.architecture,
                    status = "completed",
                    codeFiles = result.codeFiles,
                    previewComponentType = result.previewType
                )
                val newId = repository.saveProject(currentUserId, newProject)
                val created = newProject.copy(id = newId)
                _selectedProject.value = created

                // Add initial AI welcome message to chat
                val welcome = if (_isSinhala.value) {
                    "ඔබගේ '${result.appName}' Android යෙදුම සාර්ථකව නිර්මාණය කළා! පහතින් Jetpack Compose කේතය පරීක්ෂා කළ හැක."
                } else {
                    "Your '${result.appName}' Android app is ready! Inspect and refine the code below."
                }
                repository.addMessage(
                    currentUserId,
                    newId,
                    ChatMessage(
                        userId = currentUserId,
                        projectId = newId,
                        role = "assistant",
                        content = welcome
                    )
                )
            } catch (e: Exception) {
                Log.e("AppBuilderVM", "Generation error", e)
                _generationState.value = GenerationState.Error(e.localizedMessage ?: "App generation failed")
            }
        }
    }

    fun createFromTemplate(template: AppTemplate) {
        viewModelScope.launch {
            try {
                val newProject = AppProject(
                    userId = currentUserId,
                    name = template.title,
                    description = template.description,
                    category = template.category,
                    prompt = template.prompt,
                    targetSdk = 36L,
                    status = "completed",
                    codeFiles = template.initialFiles,
                    previewComponentType = template.previewComponentType
                )
                val newId = repository.saveProject(currentUserId, newProject)
                _selectedProject.value = newProject.copy(id = newId)
            } catch (e: Exception) {
                Log.e("AppBuilderVM", "Error cloning template", e)
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            try {
                repository.deleteProject(currentUserId, projectId)
                if (_selectedProject.value?.id == projectId) {
                    _selectedProject.value = null
                }
            } catch (e: Exception) {
                Log.e("AppBuilderVM", "Error deleting project", e)
            }
        }
    }

    fun sendMessageToArchitect(messageText: String) {
        val proj = _selectedProject.value ?: return
        if (messageText.isBlank()) return
        val lang = if (_isSinhala.value) "Sinhala" else "English"

        viewModelScope.launch {
            try {
                // Save user message to Firestore
                val userMsg = ChatMessage(
                    userId = currentUserId,
                    projectId = proj.id,
                    role = "user",
                    content = messageText
                )
                repository.addMessage(currentUserId, proj.id, userMsg)

                _isChatLoading.value = true
                val aiReply = aiService.chatWithArchitect(_chatMessages.value + userMsg, messageText, lang)
                
                // Save AI response to Firestore
                val aiMsg = ChatMessage(
                    userId = currentUserId,
                    projectId = proj.id,
                    role = "assistant",
                    content = aiReply
                )
                repository.addMessage(currentUserId, proj.id, aiMsg)
            } catch (e: Exception) {
                Log.e("AppBuilderVM", "Chat failed", e)
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    fun clearGenerationState() {
        _generationState.value = GenerationState.Idle
    }
}
