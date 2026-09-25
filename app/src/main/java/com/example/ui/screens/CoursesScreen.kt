package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.Bookmark
import com.example.data.local.entity.Chapter
import com.example.data.local.entity.QuizQuestion
import com.example.data.local.entity.StudyResource
import com.example.data.local.entity.Subject
import com.example.ui.components.AddChapterDialog
import com.example.ui.components.AddQuestionDialog
import com.example.ui.components.AddResourceDialog
import com.example.ui.components.AddSubjectDialog
import com.example.ui.components.ResourceViewerDialog
import com.example.ui.theme.EmeraldGreen

@Composable
fun CoursesScreen(
    subjects: List<Subject>,
    chapters: List<Chapter>,
    resources: List<StudyResource>,
    questions: List<QuizQuestion>,
    bookmarks: List<Bookmark>,
    onAddSubject: (name: String, colorHex: String) -> Unit,
    onDeleteSubject: (Subject) -> Unit,
    onAddChapter: (subjectId: Long, title: String) -> Unit,
    onToggleChapter: (Chapter) -> Unit,
    onDeleteChapter: (Chapter) -> Unit,
    onAddResource: (chapterId: Long, title: String, type: String, content: String) -> Unit,
    onDeleteResource: (StudyResource) -> Unit,
    onAddQuestion: (chapterId: Long, text: String, a: String, b: String, c: String, d: String, correct: String, exp: String) -> Unit,
    onToggleBookmark: (type: String, id: Long, title: String) -> Unit,
    onStartChapterQuiz: (List<QuizQuestion>, String, Long) -> Unit
) {
    var selectedSubjectId by remember(subjects) {
        mutableLongStateOf(subjects.firstOrNull()?.subjectId ?: 1L)
    }

    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showAddChapterDialog by remember { mutableStateOf(false) }
    var addResourceForChapter by remember { mutableStateOf<Chapter?>(null) }
    var addQuestionForChapter by remember { mutableStateOf<Chapter?>(null) }
    var viewingResource by remember { mutableStateOf<StudyResource?>(null) }

    val activeSubject = subjects.find { it.subjectId == selectedSubjectId } ?: subjects.firstOrNull()
    val subjectChapters = chapters.filter { it.subjectId == (activeSubject?.subjectId ?: -1) }

    // Dialogs
    if (showAddSubjectDialog) {
        AddSubjectDialog(
            onDismiss = { showAddSubjectDialog = false },
            onConfirm = { name, color ->
                onAddSubject(name, color)
                showAddSubjectDialog = false
            }
        )
    }

    if (showAddChapterDialog && activeSubject != null) {
        AddChapterDialog(
            subjectName = activeSubject.subjectName,
            onDismiss = { showAddChapterDialog = false },
            onConfirm = { title ->
                onAddChapter(activeSubject.subjectId, title)
                showAddChapterDialog = false
            }
        )
    }

    addResourceForChapter?.let { ch ->
        AddResourceDialog(
            chapterTitle = ch.chapterTitle,
            onDismiss = { addResourceForChapter = null },
            onConfirm = { title, type, content ->
                onAddResource(ch.chapterId, title, type, content)
                addResourceForChapter = null
            }
        )
    }

    addQuestionForChapter?.let { ch ->
        AddQuestionDialog(
            chapterTitle = ch.chapterTitle,
            onDismiss = { addQuestionForChapter = null },
            onConfirm = { text, a, b, c, d, correct, exp ->
                onAddQuestion(ch.chapterId, text, a, b, c, d, correct, exp)
                addQuestionForChapter = null
            }
        )
    }

    viewingResource?.let { res ->
        ResourceViewerDialog(
            resource = res,
            onDismiss = { viewingResource = null }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddChapterDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_chapter_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Chapter")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Topic", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("courses_screen")
        ) {
            // Subject selection Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Course Hierarchy",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { showAddSubjectDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Subject", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Subject")
                }
            }

            if (subjects.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = subjects.indexOfFirst { it.subjectId == selectedSubjectId }.coerceAtLeast(0),
                    edgePadding = 16.dp,
                    divider = {}
                ) {
                    subjects.forEach { subj ->
                        val isSelected = subj.subjectId == selectedSubjectId
                        val parsedColor = try {
                            Color(android.graphics.Color.parseColor(subj.colorHex))
                        } catch (e: Exception) {
                            MaterialTheme.colorScheme.primary
                        }

                        Tab(
                            selected = isSelected,
                            onClick = { selectedSubjectId = subj.subjectId },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(parsedColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = subj.subjectName,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chapters List
            if (activeSubject == null || subjectChapters.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Empty",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No topics in this subject yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { showAddChapterDialog = true }) {
                            Text("Add First Topic")
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Subject info header
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = activeSubject.subjectName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val completedCount = subjectChapters.count { it.status == "completed" }
                                    Text(
                                        text = "$completedCount of ${subjectChapters.size} topics completed",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (subjects.size > 1) {
                                    IconButton(onClick = { onDeleteSubject(activeSubject) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete subject",
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    items(subjectChapters) { chapter ->
                        val chapterResources = resources.filter { it.chapterId == chapter.chapterId }
                        val chapterQuestions = questions.filter { it.chapterId == chapter.chapterId }
                        val isBookmarked = bookmarks.any { it.itemType == "chapter" && it.itemId == chapter.chapterId }

                        ChapterAccordionCard(
                            chapter = chapter,
                            resources = chapterResources,
                            questions = chapterQuestions,
                            isBookmarked = isBookmarked,
                            onToggleStatus = { onToggleChapter(chapter) },
                            onToggleBookmark = {
                                onToggleBookmark("chapter", chapter.chapterId, chapter.chapterTitle)
                            },
                            onDeleteChapter = { onDeleteChapter(chapter) },
                            onAddResource = { addResourceForChapter = chapter },
                            onAddQuestion = { addQuestionForChapter = chapter },
                            onViewResource = { viewingResource = it },
                            onDeleteResource = onDeleteResource,
                            onPracticeQuiz = {
                                onStartChapterQuiz(chapterQuestions, chapter.chapterTitle, chapter.chapterId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChapterAccordionCard(
    chapter: Chapter,
    resources: List<StudyResource>,
    questions: List<QuizQuestion>,
    isBookmarked: Boolean,
    onToggleStatus: () -> Unit,
    onToggleBookmark: () -> Unit,
    onDeleteChapter: () -> Unit,
    onAddResource: () -> Unit,
    onAddQuestion: () -> Unit,
    onViewResource: (StudyResource) -> Unit,
    onDeleteResource: (StudyResource) -> Unit,
    onPracticeQuiz: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isCompleted = chapter.status == "completed"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleStatus, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Toggle completion",
                        tint = if (isCompleted) EmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { expanded = !expanded }
                ) {
                    Text(
                        text = chapter.chapterTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${resources.size} resources • ${questions.size} questions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onToggleBookmark, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { expanded = !expanded }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand chapter"
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    // Resources header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Learning Resources (${resources.size})",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onAddResource) {
                            Text("+ Add Resource", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    if (resources.isEmpty()) {
                        Text(
                            text = "No study notes or formulas uploaded yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    } else {
                        resources.forEach { res ->
                            ResourceItemRow(
                                resource = res,
                                onClick = { onViewResource(res) },
                                onDelete = { onDeleteResource(res) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Questions section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Practice Questions (${questions.size})",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onAddQuestion) {
                            Text("+ Add Question", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    if (questions.isNotEmpty()) {
                        Button(
                            onClick = onPracticeQuiz,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Practice Quiz",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Practice This Topic Quiz (${questions.size} Qs)")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDeleteChapter) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete topic",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete Topic", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResourceItemRow(
    resource: StudyResource,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val icon = when (resource.type.lowercase()) {
        "pdf" -> Icons.Default.PictureAsPdf
        "video link" -> Icons.Default.OndemandVideo
        "formula sheet" -> Icons.Default.Functions
        else -> Icons.Default.Description
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = resource.type,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = resource.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = resource.type.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete resource",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
