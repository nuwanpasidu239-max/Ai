package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AppProject
import com.example.ui.theme.CoralAccent
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.PrimaryCyan

@Composable
fun ProjectsScreen(
    projects: List<AppProject>,
    onSelectProject: (AppProject) -> Unit,
    onDeleteProject: (String) -> Unit,
    onCreateNewClick: () -> Unit,
    isSinhala: Boolean,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var projectToDelete by remember { mutableStateOf<AppProject?>(null) }

    val filteredProjects = remember(projects, searchQuery) {
        if (searchQuery.isBlank()) projects
        else projects.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Search & Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isSinhala) "මගේ Android Projects" else "My Android Projects",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = if (isSinhala)
                        "Cloud Firestore තුළ සුරකින ලද Apps: ${projects.size}"
                    else
                        "Saved in Cloud Firestore: ${projects.size} Apps",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }
            IconButton(
                onClick = onCreateNewClick,
                colors = IconButtonDefaults.iconButtonColors(containerColor = PrimaryCyan.copy(alpha = 0.2f))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create New", tint = PrimaryCyan)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (isSinhala) "Projects සොයන්න..." else "Search projects...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryCyan,
                unfocusedBorderColor = Color(0xFF374151),
                focusedContainerColor = Color(0xFF111827),
                unfocusedContainerColor = Color(0xFF111827)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredProjects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isSinhala) "තවමත් Projects නොමැත" else "No Projects Yet",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = if (isSinhala)
                            "Studio වෙත ගොස් ඔබගේ පළමු Android App එක නිර්මාණය කරන්න."
                        else
                            "Head over to the Studio tab to architect your first app with AI.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onCreateNewClick,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = Color(0xFF00363D))
                    ) {
                        Text(if (isSinhala) "නව App එකක් හදන්න" else "Create New App")
                    }
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredProjects, key = { it.id }) { project ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                            .clickable { onSelectProject(project) }
                            .testTag("project_item_${project.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = project.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = project.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF94A3B8),
                                        maxLines = 2
                                    )
                                }
                                IconButton(
                                    onClick = { projectToDelete = project },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.DeleteOutline,
                                        contentDescription = "Delete",
                                        tint = CoralAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(project.category, style = MaterialTheme.typography.labelSmall) }
                                    )
                                    AssistChip(
                                        onClick = {},
                                        label = { Text("${project.codeFiles.size} Files", style = MaterialTheme.typography.labelSmall) }
                                    )
                                }

                                TextButton(onClick = { onSelectProject(project) }) {
                                    Text(if (isSinhala) "කේතය බලන්න >" else "Inspect Code >", color = PrimaryCyan)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    projectToDelete?.let { proj ->
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = { Text(if (isSinhala) "Project එක මකන්නද?" else "Delete Project?") },
            text = {
                Text(
                    if (isSinhala)
                        "'${proj.name}' ව්‍යාපෘතිය Cloud Firestore වෙතින් ස්ථිරවම මකා දැමෙනු ඇත."
                    else
                        "Are you sure you want to delete '${proj.name}'? This action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteProject(proj.id)
                        projectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralAccent)
                ) {
                    Text(if (isSinhala) "ඔව්, මකන්න" else "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToDelete = null }) {
                    Text(if (isSinhala) "අවලංගු කරන්න" else "Cancel")
                }
            }
        )
    }
}
