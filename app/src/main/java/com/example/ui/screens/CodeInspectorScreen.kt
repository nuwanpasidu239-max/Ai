package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppProject
import com.example.data.model.ChatMessage
import com.example.ui.theme.CodeBg
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.PrimaryCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeInspectorScreen(
    project: AppProject,
    chatMessages: List<ChatMessage>,
    isChatLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit,
    isSinhala: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val files = remember(project) { project.codeFiles.toList() }
    var selectedFileIndex by remember { mutableIntStateOf(0) }
    var showChatSheet by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }

    val currentFile = files.getOrNull(selectedFileIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(project.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(
                            text = "${project.category} • Target SDK ${project.targetSdk}",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryCyan
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showChatSheet = true }) {
                        Badge(containerColor = PrimaryCyan) {
                            Icon(Icons.Default.SmartToy, contentDescription = "AI Architect Chat", tint = Color(0xFF00363D))
                        }
                    }
                    IconButton(
                        onClick = {
                            currentFile?.second?.let { code ->
                                clipboardManager.setText(AnnotatedString(code))
                                Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("copy_code_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Code")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showChatSheet = true },
                containerColor = PrimaryCyan,
                contentColor = Color(0xFF00363D),
                icon = { Icon(Icons.Default.SmartToy, contentDescription = null) },
                text = { Text(if (isSinhala) "AI Architect ගෙන් අසන්න" else "Ask AI Architect") }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFF090D16))
        ) {
            // File tabs
            if (files.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = selectedFileIndex,
                    containerColor = Color(0xFF111827),
                    contentColor = PrimaryCyan,
                    edgePadding = 12.dp
                ) {
                    files.forEachIndexed { index, (fileName, _) ->
                        Tab(
                            selected = selectedFileIndex == index,
                            onClick = { selectedFileIndex = index },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (fileName.endsWith(".kt")) Icons.Default.Code else Icons.Default.Description,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = if (selectedFileIndex == index) PrimaryCyan else Color(0xFF94A3B8)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = fileName,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = if (selectedFileIndex == index) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            }
                        )
                    }
                }
            }

            // Code Content Box
            val code = currentFile?.second ?: "// No code files available"
            val hScroll = rememberScrollState()
            val vScroll = rememberScrollState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CodeBg)
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(vScroll)
                        .horizontalScroll(hScroll)
                        .padding(14.dp)
                ) {
                    // Line numbers
                    val lineCount = code.lines().size
                    val lineNumbers = (1..lineCount).joinToString("\n")
                    Text(
                        text = lineNumbers,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )

                    // Code text
                    Text(
                        text = code,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color(0xFFE2E8F0),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    // AI Architect Chat Bottom Sheet
    if (showChatSheet) {
        ModalBottomSheet(
            onDismissRequest = { showChatSheet = false },
            containerColor = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SmartToy, contentDescription = null, tint = PrimaryCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSinhala) "AI Architect සාකච්ඡාව" else "AI App Architect Chat",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    IconButton(onClick = { showChatSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                HorizontalDivider(color = Color(0xFF1E293B), modifier = Modifier.padding(vertical = 8.dp))

                // Messages list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    reverseLayout = true,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isChatLoading) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = PrimaryCyan, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isSinhala) "පිළිතුර සකසමින්..." else "Architect is thinking...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }

                    items(chatMessages.reversed()) { msg ->
                        val isUser = msg.role == "user"
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 14.dp,
                                            topEnd = 14.dp,
                                            bottomStart = if (isUser) 14.dp else 2.dp,
                                            bottomEnd = if (isUser) 2.dp else 14.dp
                                        )
                                    )
                                    .background(if (isUser) PrimaryCyan.copy(alpha = 0.2f) else Color(0xFF1E293B))
                                    .padding(12.dp)
                                    .widthIn(max = 280.dp)
                            ) {
                                Text(
                                    text = msg.content,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isUser) PrimaryCyan else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chat Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        placeholder = {
                            Text(
                                if (isSinhala) "කේතය වෙනස් කිරීමට උපදෙස් දෙන්න..."
                                else "Ask to refactor, add features, or explain..."
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = Color(0xFF374151)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (chatInput.isNotBlank()) {
                                onSendMessage(chatInput)
                                chatInput = ""
                            }
                        },
                        enabled = !isChatLoading && chatInput.isNotBlank(),
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (chatInput.isNotBlank()) PrimaryCyan else Color(0xFF1F2937))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (chatInput.isNotBlank()) Color(0xFF00363D) else Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}
