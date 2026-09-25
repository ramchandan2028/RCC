package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.schema.SchemaDocumentation
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.VioletPurple

@Composable
fun SchemaViewerScreen(
    userCount: Int,
    subjectCount: Int,
    chapterCount: Int,
    resourceCount: Int,
    questionCount: Int,
    quizCount: Int,
    taskCount: Int,
    focusSessionCount: Int,
    bookmarkCount: Int,
    onResetDatabase: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var copiedMessage by remember { mutableStateOf<String?>(null) }

    val tabs = listOf("SQL DDL", "NoSQL (JSON)", "Dummy Payloads", "ER Explanation", "Live DB Inspector")

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        copiedMessage = "Copied $label to clipboard!"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("schema_viewer_screen")
    ) {
        // Top Banner
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = "Schema & Architecture",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Relational DDL, NoSQL Document Schema, Dummy Payloads & ER Links",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        selectedTab = index
                        copiedMessage = null
                    },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedTab) {
            0 -> CodeViewerTab(
                title = "PostgreSQL / Relational DDL (9 Tables)",
                code = SchemaDocumentation.SQL_DDL_POSTGRES,
                onCopy = { copyToClipboard(SchemaDocumentation.SQL_DDL_POSTGRES, "SQL DDL") },
                copiedNotice = copiedMessage
            )
            1 -> CodeViewerTab(
                title = "NoSQL Firestore Document Hierarchy",
                code = SchemaDocumentation.NOSQL_STRUCTURE,
                onCopy = { copyToClipboard(SchemaDocumentation.NOSQL_STRUCTURE, "NoSQL Document Structure") },
                copiedNotice = copiedMessage
            )
            2 -> CodeViewerTab(
                title = "Sample Dummy Payloads (All 9 Modules)",
                code = SchemaDocumentation.DUMMY_JSON_PAYLOADS,
                onCopy = { copyToClipboard(SchemaDocumentation.DUMMY_JSON_PAYLOADS, "Dummy Payloads") },
                copiedNotice = copiedMessage
            )
            3 -> ErArchitectureTab(
                explanation = SchemaDocumentation.ER_EXPLANATION,
                onCopy = { copyToClipboard(SchemaDocumentation.ER_EXPLANATION, "ER Architecture Explanation") }
            )
            4 -> LiveDatabaseInspectorTab(
                userCount = userCount,
                subjectCount = subjectCount,
                chapterCount = chapterCount,
                resourceCount = resourceCount,
                questionCount = questionCount,
                quizCount = quizCount,
                taskCount = taskCount,
                focusSessionCount = focusSessionCount,
                bookmarkCount = bookmarkCount,
                onResetDatabase = onResetDatabase
            )
        }
    }
}

@Composable
fun CodeViewerTab(
    title: String,
    code: String,
    onCopy: () -> Unit,
    copiedNotice: String?
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(onClick = onCopy) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (copiedNotice != null) "Copied!" else "Copy", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E293B) // Dark slate for readable code
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    Text(
                        text = code,
                        color = Color(0xFFE2E8F0),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ErArchitectureTab(
    explanation: String,
    onCopy: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Entity-Relationship Mapping",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(onClick = onCopy) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy ER Explanation", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy")
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Visual Relationship Matrix",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    RelationshipLinkRow("user_profiles", "1 : N", "subjects (ON DELETE CASCADE)")
                    RelationshipLinkRow("subjects", "1 : N", "chapters (ON DELETE CASCADE)")
                    RelationshipLinkRow("chapters", "1 : N", "resources (ON DELETE CASCADE)")
                    RelationshipLinkRow("chapters", "1 : N", "questions (ON DELETE CASCADE)")
                    RelationshipLinkRow("user_profiles", "1 : N", "study_tasks (ON DELETE CASCADE)")
                    RelationshipLinkRow("user_profiles", "1 : N", "focus_sessions (ON DELETE CASCADE)")
                    RelationshipLinkRow("subjects", "1 : N", "focus_sessions (ON DELETE CASCADE)")
                    RelationshipLinkRow("user_profiles", "1 : N", "quiz_history (ON DELETE CASCADE)")
                    RelationshipLinkRow("chapters", "1 : N", "quiz_history (ON DELETE SET NULL)")
                    RelationshipLinkRow("user_profiles", "1 : N", "bookmarks (ON DELETE CASCADE)")
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RelationshipLinkRow(source: String, relation: String, target: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = source,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = relation,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text("➔", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(6.dp))
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = target,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun LiveDatabaseInspectorTab(
    userCount: Int,
    subjectCount: Int,
    chapterCount: Int,
    resourceCount: Int,
    questionCount: Int,
    quizCount: Int,
    taskCount: Int,
    focusSessionCount: Int,
    bookmarkCount: Int,
    onResetDatabase: () -> Unit
) {
    val tables = listOf(
        TableMetric("user_profiles", "User profile and streak settings", userCount, Icons.Default.AccountCircle, SkyBlue),
        TableMetric("subjects", "Academic courses & subjects", subjectCount, Icons.Default.MenuBook, EmeraldGreen),
        TableMetric("chapters", "Subject topics and completion status", chapterCount, Icons.Default.Description, VioletPurple),
        TableMetric("resources", "PDFs, notes, formula sheets, videos", resourceCount, Icons.Default.Storage, AmberOrange),
        TableMetric("questions", "MCQ question bank with explanations", questionCount, Icons.Default.Quiz, SkyBlue),
        TableMetric("quiz_history", "Recorded exam attempts & scores", quizCount, Icons.Default.Assignment, EmeraldGreen),
        TableMetric("study_tasks", "Actionable to-dos with due dates", taskCount, Icons.Default.Assignment, VioletPurple),
        TableMetric("focus_sessions", "Pomodoro study logs & duration", focusSessionCount, Icons.Default.HourglassBottom, AmberOrange),
        TableMetric("bookmarks", "Saved pins for quick revision", bookmarkCount, Icons.Default.Bookmark, EmeraldGreen)
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Live SQLite / Room Inspector",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time records stored in Android Room DB",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(onClick = onResetDatabase) {
                    Icon(Icons.Default.Refresh, contentDescription = "Re-seed DB", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Re-Seed DB", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        items(tables) { table ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(table.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = table.icon,
                                contentDescription = table.tableName,
                                tint = table.color,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = table.tableName,
                                style = MaterialTheme.typography.titleSmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = table.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${table.count} rows",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

data class TableMetric(
    val tableName: String,
    val description: String,
    val count: Int,
    val icon: ImageVector,
    val color: Color
)
