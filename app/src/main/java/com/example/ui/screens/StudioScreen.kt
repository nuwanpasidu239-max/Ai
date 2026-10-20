package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.GeneratedAppResult
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.PrimaryCyan
import com.example.ui.viewmodel.GenerationState

@Composable
fun StudioScreen(
    generationState: GenerationState,
    onGenerate: (prompt: String, category: String) -> Unit,
    onOpenProject: () -> Unit,
    onOpenPlayground: () -> Unit,
    isSinhala: Boolean,
    modifier: Modifier = Modifier
) {
    var promptText by remember {
        mutableStateOf(
            if (isSinhala) "මට දිනපතා වතුර පානය සහ සෞඛ්‍යය නිරීක්ෂණය කරන Android App එකක් හදන්න"
            else "Build a daily water hydration & health tracker with animated progress ring and reminder logging"
        )
    }
    var selectedCategory by remember { mutableStateOf("Health & Fitness") }
    val categories = listOf("Health & Fitness", "Productivity", "E-Commerce", "Finance", "Social & Chat", "Utility")

    val quickPrompts = if (isSinhala) {
        listOf(
            "Fitness Step & Workout Tracker",
            "Crypto & Stock Portfolio Tracker",
            "Recipe & Meal Planner App",
            "Task & Project Kanban Board",
            "Music & Audio Podcast Player"
        )
    } else {
        listOf(
            "Fitness Step & Workout Tracker",
            "Crypto Portfolio & Market Tracker",
            "Recipe & Meal Planner App",
            "Task & Project Kanban Board",
            "Music & Podcast Player UI"
        )
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Hero Header
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    Brush.horizontalGradient(listOf(PrimaryCyan.copy(alpha = 0.5f), ElectricPurple.copy(alpha = 0.5f))),
                    RoundedCornerShape(20.dp)
                )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(PrimaryCyan.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryCyan)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isSinhala) "AI Android App Studio" else "AI Android App Architect",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = if (isSinhala) "Gemini 3.8 Flash • Compose • MVVM" else "Powered by Gemini 3.8 Flash • Jetpack Compose",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isSinhala)
                        "ඔබට අවශ්‍ය ඕනෑම Android App එකක් විස්තර කරන්න. AppCraft AI මඟින් සම්පූර්ණ Jetpack Compose UI, ViewModel, සහ Data Models ස්වයංක්‍රීයව නිර්මාණය කර දෙනු ඇත."
                    else
                        "Describe any Android application you want to build. AppCraft AI instantly architects, designs, and codes complete Compose screens, ViewModels, and state management.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8),
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Prompt Input Area
        Text(
            text = if (isSinhala) "ඔබේ App අදහස (App Prompt)" else "Describe Your Android App",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = promptText,
            onValueChange = { promptText = it },
            placeholder = {
                Text(
                    if (isSinhala) "උදා: මට E-Commerce Shop App එකක් හදන්න..."
                    else "e.g. Build an expense manager with charts, budget limits, and category tags..."
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .testTag("app_prompt_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryCyan,
                unfocusedBorderColor = Color(0xFF374151),
                focusedContainerColor = Color(0xFF111827),
                unfocusedContainerColor = Color(0xFF111827)
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Ideas Chips
        Text(
            text = if (isSinhala) "ඉක්මන් අදහස් (Quick Suggestions)" else "Quick Starters",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF94A3B8)
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(quickPrompts) { quickPrompt ->
                SuggestionChip(
                    onClick = { promptText = quickPrompt },
                    label = { Text(quickPrompt, style = MaterialTheme.typography.labelSmall) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Color(0xFF1F2937),
                        labelColor = Color(0xFFE2E8F0)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category Filter
        Text(
            text = if (isSinhala) "කාණ්ඩය (Category)" else "App Category",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF94A3B8)
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryCyan.copy(alpha = 0.2f),
                        selectedLabelColor = PrimaryCyan
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Generate Action Button
        val isGenerating = generationState is GenerationState.Generating
        Button(
            onClick = { onGenerate(promptText, selectedCategory) },
            enabled = !isGenerating && promptText.isNotBlank(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryCyan,
                contentColor = Color(0xFF00363D)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("generate_app_button")
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color(0xFF00363D),
                    strokeWidth = 2.5.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                val stepText = (generationState as GenerationState.Generating).step
                Text(
                    text = stepText,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            } else {
                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSinhala) "Android App එක Generate කරන්න" else "Architect & Generate App",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // Error Feedback
        if (generationState is GenerationState.Error) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF451A1A))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFFF8B8B))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = generationState.message,
                        color = Color(0xFFFFD1D1),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Success Card
        if (generationState is GenerationState.Success) {
            Spacer(modifier = Modifier.height(20.dp))
            val result = generationState.result

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111F1D)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonEmerald, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonEmerald)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSinhala) "App එක සාර්ථකව නිර්මාණය විය!" else "Android App Generated Successfully!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NeonEmerald
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = result.appName,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = result.tagline,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = {},
                            label = { Text(result.category) }
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text("${result.codeFiles.size} Files") }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onOpenProject,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Color(0xFF003920)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("inspect_code_button")
                        ) {
                            Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isSinhala) "කේතය බලන්න" else "Inspect Code")
                        }
                        OutlinedButton(
                            onClick = onOpenPlayground,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isSinhala) "Playground" else "Playground")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
