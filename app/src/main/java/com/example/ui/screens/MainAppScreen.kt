package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppProject
import com.example.data.model.AppTemplate
import com.example.data.templates.AppTemplates
import com.example.ui.theme.PrimaryCyan
import com.example.ui.viewmodel.AppBuilderViewModel
import com.google.firebase.auth.FirebaseUser

enum class AppDestination(val labelEn: String, val labelSi: String, val icon: ImageVector) {
    STUDIO("Studio", "Studio", Icons.Default.AutoAwesome),
    PROJECTS("Projects", "Projects", Icons.Default.Folder),
    TEMPLATES("Templates", "Templates", Icons.Default.Layers),
    PLAYGROUND("Playground", "Playground", Icons.Default.PlayCircle)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    user: FirebaseUser,
    viewModel: AppBuilderViewModel,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(AppDestination.STUDIO) }
    val isSinhala by viewModel.isSinhala.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val selectedProject by viewModel.selectedProject.collectAsState()
    val generationState by viewModel.generationState.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()

    // Playground States
    val steps by viewModel.playgroundCounter.collectAsState()
    val todos by viewModel.playgroundTodos.collectAsState()
    val cart by viewModel.playgroundCart.collectAsState()

    // Handle back press when viewing a project code inspector
    BackHandler(enabled = selectedProject != null) {
        viewModel.selectProject(null)
    }

    if (selectedProject != null) {
        CodeInspectorScreen(
            project = selectedProject!!,
            chatMessages = chatMessages,
            isChatLoading = isChatLoading,
            onSendMessage = { viewModel.sendMessageToArchitect(it) },
            onBack = { viewModel.selectProject(null) },
            isSinhala = isSinhala
        )
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AppCraft AI",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color.White
                            )
                        }
                    },
                    navigationIcon = {
                        FilterChip(
                            selected = isSinhala,
                            onClick = { viewModel.toggleLanguage() },
                            label = { Text(if (isSinhala) "සිංහල" else "EN", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .testTag("language_toggle_button")
                        )
                    },
                    actions = {
                        // User Avatar
                        if (user.photoUrl != null) {
                            AsyncImage(
                                model = user.photoUrl,
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, PrimaryCyan, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.displayName?.take(1)?.uppercase() ?: "U",
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan
                                )
                            }
                        }
                        IconButton(onClick = onSignOut, modifier = Modifier.testTag("sign_out_button")) {
                            Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = Color(0xFF94A3B8))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF0F172A)
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF0F172A),
                    contentColor = PrimaryCyan,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    AppDestination.values().forEach { destination ->
                        val selected = currentDestination == destination
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentDestination = destination },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.labelEn
                                )
                            },
                            label = {
                                Text(
                                    text = if (isSinhala) destination.labelSi else destination.labelEn,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF00363D),
                                indicatorColor = PrimaryCyan,
                                unselectedIconColor = Color(0xFF64748B),
                                unselectedTextColor = Color(0xFF64748B),
                                selectedTextColor = PrimaryCyan
                            )
                        )
                    }
                }
            },
            modifier = modifier.fillMaxSize()
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color(0xFF090D16))
            ) {
                when (currentDestination) {
                    AppDestination.STUDIO -> StudioScreen(
                        generationState = generationState,
                        onGenerate = { prompt, category -> viewModel.generateAppWithAi(prompt, category) },
                        onOpenProject = {
                            if (generationState is com.example.ui.viewmodel.GenerationState.Success) {
                                // Already selected by VM
                            }
                        },
                        onOpenPlayground = { currentDestination = AppDestination.PLAYGROUND },
                        isSinhala = isSinhala
                    )
                    AppDestination.PROJECTS -> ProjectsScreen(
                        projects = projects,
                        onSelectProject = { viewModel.selectProject(it) },
                        onDeleteProject = { viewModel.deleteProject(it) },
                        onCreateNewClick = { currentDestination = AppDestination.STUDIO },
                        isSinhala = isSinhala
                    )
                    AppDestination.TEMPLATES -> TemplatesScreen(
                        templates = AppTemplates.list,
                        onCloneTemplate = { template ->
                            viewModel.createFromTemplate(template)
                            currentDestination = AppDestination.PROJECTS
                        },
                        onInspectTemplate = { template ->
                            viewModel.selectProject(
                                AppProject(
                                    id = template.id,
                                    name = template.title,
                                    description = template.description,
                                    category = template.category,
                                    prompt = template.prompt,
                                    codeFiles = template.initialFiles
                                )
                            )
                        },
                        isSinhala = isSinhala
                    )
                    AppDestination.PLAYGROUND -> PlaygroundScreen(
                        steps = steps,
                        onAddSteps = { viewModel.incrementCounter() },
                        onResetSteps = { viewModel.resetCounter() },
                        todos = todos,
                        onToggleTodo = { viewModel.toggleTodo(it) },
                        onAddTodo = { viewModel.addTodo(it) },
                        cartItems = cart,
                        onAddToCart = { name, price -> viewModel.addToCart(name, price) },
                        onClearCart = { viewModel.clearCart() },
                        isSinhala = isSinhala
                    )
                }
            }
        }
    }
}
