package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.example.data.local.entity.QuizResult
import com.example.data.local.entity.Subject
import com.example.ui.ActiveQuizState
import com.example.ui.components.AddQuestionDialog
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoseRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun QuizPracticeScreen(
    quizState: ActiveQuizState,
    subjects: List<Subject>,
    chapters: List<Chapter>,
    allQuestions: List<QuizQuestion>,
    quizHistory: List<QuizResult>,
    bookmarks: List<Bookmark>,
    onStartQuiz: (questions: List<QuizQuestion>, title: String, chapterId: Long?) -> Unit,
    onSelectOption: (String) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onResetQuiz: () -> Unit,
    onToggleBookmark: (type: String, id: Long, title: String) -> Unit,
    onAddQuestion: (chapterId: Long, text: String, a: String, b: String, c: String, d: String, correct: String, exp: String) -> Unit
) {
    var selectedSubjectFilter by remember { mutableLongStateOf(0L) } // 0L means All Subjects
    var showAddQuestionDialog by remember { mutableStateOf(false) }

    val filteredQuestions = if (selectedSubjectFilter == 0L) {
        allQuestions
    } else {
        val chapterIdsInSubject = chapters.filter { it.subjectId == selectedSubjectFilter }.map { it.chapterId }
        allQuestions.filter { it.chapterId in chapterIdsInSubject }
    }

    if (showAddQuestionDialog && chapters.isNotEmpty()) {
        val firstChapter = chapters.first()
        AddQuestionDialog(
            chapterTitle = firstChapter.chapterTitle,
            onDismiss = { showAddQuestionDialog = false },
            onConfirm = { text, a, b, c, d, correct, exp ->
                onAddQuestion(firstChapter.chapterId, text, a, b, c, d, correct, exp)
                showAddQuestionDialog = false
            }
        )
    }

    // If a quiz is active and not finished
    if (quizState.questions.isNotEmpty() && !quizState.isFinished) {
        ActiveQuizRunningView(
            quizState = quizState,
            bookmarks = bookmarks,
            onSelectOption = onSelectOption,
            onSubmitAnswer = onSubmitAnswer,
            onNextQuestion = onNextQuestion,
            onQuitQuiz = onResetQuiz,
            onToggleBookmark = onToggleBookmark
        )
    } else if (quizState.isFinished) {
        QuizFinishedView(
            quizState = quizState,
            onRetake = {
                onStartQuiz(quizState.questions, quizState.subjectTitle, quizState.chapterId)
            },
            onDone = onResetQuiz
        )
    } else {
        // Selection & History view
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("quiz_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Practice & Quiz",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${allQuestions.size} Questions available in bank",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (chapters.isNotEmpty()) {
                        OutlinedButton(onClick = { showAddQuestionDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Question", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Q", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Subject Filter Chips
            item {
                Column {
                    Text(
                        text = "Select Practice Domain",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedSubjectFilter == 0L,
                            onClick = { selectedSubjectFilter = 0L },
                            label = { Text("All Subjects (${allQuestions.size})") }
                        )
                        subjects.take(2).forEach { subj ->
                            val count = chapters.filter { it.subjectId == subj.subjectId }.flatMap { ch ->
                                allQuestions.filter { it.chapterId == ch.chapterId }
                            }.size
                            FilterChip(
                                selected = selectedSubjectFilter == subj.subjectId,
                                onClick = { selectedSubjectFilter = subj.subjectId },
                                label = { Text("${subj.subjectName.split(" ").first()} ($count)") }
                            )
                        }
                    }
                    if (subjects.size > 2) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            subjects.drop(2).forEach { subj ->
                                val count = chapters.filter { it.subjectId == subj.subjectId }.flatMap { ch ->
                                    allQuestions.filter { it.chapterId == ch.chapterId }
                                }.size
                                FilterChip(
                                    selected = selectedSubjectFilter == subj.subjectId,
                                    onClick = { selectedSubjectFilter = subj.subjectId },
                                    label = { Text("${subj.subjectName.split(" ").first()} ($count)") }
                                )
                            }
                        }
                    }
                }
            }

            // Launch Practice Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Quiz,
                                    contentDescription = "Quiz Launch",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (selectedSubjectFilter == 0L) "Comprehensive Mock Test"
                                    else subjects.find { it.subjectId == selectedSubjectFilter }?.subjectName ?: "Domain Quiz",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${filteredQuestions.size} Multiple-Choice Questions",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val title = if (selectedSubjectFilter == 0L) "Comprehensive Test"
                                else subjects.find { it.subjectId == selectedSubjectFilter }?.subjectName ?: "Subject Test"
                                onStartQuiz(filteredQuestions, title, null)
                            },
                            enabled = filteredQuestions.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("start_quiz_btn")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Start Quiz")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Start Practice Quiz", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Quiz History section
            item {
                Text(
                    text = "Historical Results (${quizHistory.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (quizHistory.isEmpty()) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No recorded quiz results yet. Complete your first practice test!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(quizHistory) { quiz ->
                    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()) }
                    val percentage = ((quiz.quizScore.toFloat() / quiz.totalQuestions) * 100).toInt()
                    val badgeColor = if (percentage >= 80) EmeraldGreen else if (percentage >= 60) AmberOrange else RoseRed

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = quiz.subjectName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dateFormat.format(Date(quiz.timestamp)),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                color = badgeColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${quiz.quizScore}/${quiz.totalQuestions} ($percentage%)",
                                    color = badgeColor,
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
    }
}

@Composable
fun ActiveQuizRunningView(
    quizState: ActiveQuizState,
    bookmarks: List<Bookmark>,
    onSelectOption: (String) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onQuitQuiz: () -> Unit,
    onToggleBookmark: (type: String, id: Long, title: String) -> Unit
) {
    val currentQuestion = quizState.questions.getOrNull(quizState.currentIndex) ?: return
    val progress = (quizState.currentIndex + 1).toFloat() / quizState.questions.size
    val isBookmarked = bookmarks.any { it.itemType == "question" && it.itemId == currentQuestion.questionId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = quizState.subjectTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onQuitQuiz) {
                    Text("Exit Quiz", color = MaterialTheme.colorScheme.error)
                }
            }
        }

        // Progress bar
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Question ${quizState.currentIndex + 1} of ${quizState.questions.size}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Score: ${quizState.score}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }

        // Question Statement Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = currentQuestion.questionText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                onToggleBookmark("question", currentQuestion.questionId, currentQuestion.questionText)
                            }
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark Question",
                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Options A, B, C, D
        val options = listOf(
            "A" to currentQuestion.optionA,
            "B" to currentQuestion.optionB,
            "C" to currentQuestion.optionC,
            "D" to currentQuestion.optionD
        )

        items(options) { (key, text) ->
            val isSelected = quizState.selectedOption == key
            val isCorrectKey = key == currentQuestion.correctOption

            // Color calculation based on submitted state
            val containerColor = when {
                !quizState.isSubmitted && isSelected -> MaterialTheme.colorScheme.primaryContainer
                quizState.isSubmitted && isCorrectKey -> EmeraldGreen.copy(alpha = 0.2f)
                quizState.isSubmitted && isSelected && !isCorrectKey -> RoseRed.copy(alpha = 0.2f)
                else -> MaterialTheme.colorScheme.surface
            }

            val borderColor = when {
                !quizState.isSubmitted && isSelected -> MaterialTheme.colorScheme.primary
                quizState.isSubmitted && isCorrectKey -> EmeraldGreen
                quizState.isSubmitted && isSelected && !isCorrectKey -> RoseRed
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            }

            Card(
                onClick = { onSelectOption(key) },
                enabled = !quizState.isSubmitted,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiz_option_$key"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = containerColor),
                border = BorderStroke(1.5.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = key,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                    if (quizState.isSubmitted && isCorrectKey) {
                        Icon(Icons.Default.Check, contentDescription = "Correct", tint = EmeraldGreen)
                    } else if (quizState.isSubmitted && isSelected && !isCorrectKey) {
                        Icon(Icons.Default.Close, contentDescription = "Incorrect", tint = RoseRed)
                    }
                }
            }
        }

        // Explanation Card (shows after submit)
        if (quizState.isSubmitted) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (quizState.isCorrect) EmeraldGreen.copy(alpha = 0.1f)
                        else RoseRed.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (quizState.isCorrect) "✓ Correct Answer!" else "✗ Incorrect",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (quizState.isCorrect) EmeraldGreen else RoseRed
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Correct Option: ${currentQuestion.correctOption}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentQuestion.explanation,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Action Buttons
        item {
            Spacer(modifier = Modifier.height(8.dp))
            if (!quizState.isSubmitted) {
                Button(
                    onClick = onSubmitAnswer,
                    enabled = quizState.selectedOption != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_answer_btn")
                ) {
                    Text("Submit Answer", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onNextQuestion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("next_question_btn")
                ) {
                    Text(
                        if (quizState.currentIndex + 1 < quizState.questions.size) "Next Question →"
                        else "Finish Quiz & View Results",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun QuizFinishedView(
    quizState: ActiveQuizState,
    onRetake: () -> Unit,
    onDone: () -> Unit
) {
    val total = quizState.questions.size
    val score = quizState.score
    val percentage = if (total > 0) ((score.toFloat() / total) * 100).toInt() else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Trophy",
                tint = AmberOrange,
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (percentage >= 80) "Outstanding Performance! 🌟"
            else if (percentage >= 60) "Good Effort! Keep Learning 📚"
            else "Keep Practicing! 💪",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Domain: ${quizState.subjectTitle}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$score / $total",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Score", style = MaterialTheme.typography.labelMedium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (percentage >= 70) EmeraldGreen else AmberOrange
                    )
                    Text("Accuracy", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Back to Quizzes", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onRetake,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Retake")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retake Quiz")
        }
    }
}
