package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Schema
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.StudyViewModel
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.CoursesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FocusTimerScreen
import com.example.ui.screens.QuizPracticeScreen
import com.example.ui.screens.SchemaViewerScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.MyApplicationTheme

enum class AppScreen(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    COURSES("Courses", Icons.Default.MenuBook),
    QUIZ("Quiz", Icons.Default.Quiz),
    FOCUS("Focus", Icons.Default.HourglassBottom),
    SCHEMA("Schema & ER", Icons.Default.Schema),
    TASKS("Tasks", Icons.Default.Assignment),
    BOOKMARKS("Bookmarks", Icons.Default.Bookmark)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                StudyHubApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyHubApp(studyViewModel: StudyViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect States
    val userProfile by studyViewModel.userProfile.collectAsStateWithLifecycle()
    val subjects by studyViewModel.subjects.collectAsStateWithLifecycle()
    val chapters by studyViewModel.chapters.collectAsStateWithLifecycle()
    val resources by studyViewModel.resources.collectAsStateWithLifecycle()
    val questions by studyViewModel.questions.collectAsStateWithLifecycle()
    val quizHistory by studyViewModel.quizHistory.collectAsStateWithLifecycle()
    val tasks by studyViewModel.tasks.collectAsStateWithLifecycle()
    val focusSessions by studyViewModel.focusSessions.collectAsStateWithLifecycle()
    val todayFocusMinutes by studyViewModel.todayFocusMinutes.collectAsStateWithLifecycle()
    val bookmarks by studyViewModel.bookmarks.collectAsStateWithLifecycle()

    val quizState by studyViewModel.quizState.collectAsStateWithLifecycle()
    val pomodoroRunning by studyViewModel.pomodoroRunning.collectAsStateWithLifecycle()
    val pomodoroSecondsLeft by studyViewModel.pomodoroSecondsLeft.collectAsStateWithLifecycle()
    val pomodoroTotalSeconds by studyViewModel.pomodoroTotalSeconds.collectAsStateWithLifecycle()
    val selectedPomodoroSubjectId by studyViewModel.selectedPomodoroSubjectId.collectAsStateWithLifecycle()

    val userMessage by studyViewModel.userMessage.collectAsStateWithLifecycle()

    // Inspector counts
    val userCount by studyViewModel.userCount.collectAsStateWithLifecycle()
    val subjectCount by studyViewModel.subjectCount.collectAsStateWithLifecycle()
    val chapterCount by studyViewModel.chapterCount.collectAsStateWithLifecycle()
    val resourceCount by studyViewModel.resourceCount.collectAsStateWithLifecycle()
    val questionCount by studyViewModel.questionCount.collectAsStateWithLifecycle()
    val quizCount by studyViewModel.quizCount.collectAsStateWithLifecycle()
    val taskCount by studyViewModel.taskCount.collectAsStateWithLifecycle()
    val focusSessionCount by studyViewModel.focusSessionCount.collectAsStateWithLifecycle()
    val bookmarkCount by studyViewModel.bookmarkCount.collectAsStateWithLifecycle()

    // Show Snackbars
    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            studyViewModel.clearUserMessage()
        }
    }

    // Handle back button when not on Dashboard
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        currentScreen = AppScreen.DASHBOARD
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentScreen) {
                            AppScreen.DASHBOARD -> "RCC"
                            AppScreen.COURSES -> "Course Hierarchy"
                            AppScreen.QUIZ -> "Practice & Quiz"
                            AppScreen.FOCUS -> "Pomodoro Focus"
                            AppScreen.SCHEMA -> "Schema & Architecture"
                            AppScreen.TASKS -> "Study Tasks"
                            AppScreen.BOOKMARKS -> "Bookmarks"
                        },
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    // Task badge action
                    val pendingCount = tasks.count { it.status == "pending" }
                    IconButton(
                        onClick = { currentScreen = AppScreen.TASKS },
                        modifier = Modifier.testTag("top_bar_tasks_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                if (pendingCount > 0) {
                                    Badge { Text("$pendingCount") }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = "Tasks",
                                tint = if (currentScreen == AppScreen.TASKS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Bookmarks badge action
                    IconButton(
                        onClick = { currentScreen = AppScreen.BOOKMARKS },
                        modifier = Modifier.testTag("top_bar_bookmarks_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                if (bookmarks.isNotEmpty()) {
                                    Badge { Text("${bookmarks.size}") }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Bookmarks",
                                tint = if (currentScreen == AppScreen.BOOKMARKS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Schema explorer shortcut action
                    IconButton(
                        onClick = { currentScreen = AppScreen.SCHEMA },
                        modifier = Modifier.testTag("top_bar_schema_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schema,
                            contentDescription = "Schema Architecture",
                            tint = if (currentScreen == AppScreen.SCHEMA) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                val primaryTabs = listOf(
                    AppScreen.DASHBOARD,
                    AppScreen.COURSES,
                    AppScreen.QUIZ,
                    AppScreen.FOCUS,
                    AppScreen.SCHEMA
                )
                primaryTabs.forEach { screen ->
                    NavigationBarItem(
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.label
                            )
                        },
                        label = { Text(screen.label) },
                        modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(
                    userProfile = userProfile,
                    subjects = subjects,
                    chapters = chapters,
                    tasks = tasks,
                    todayFocusMinutes = todayFocusMinutes ?: 0,
                    quizHistory = quizHistory,
                    onNavigateToCourses = { currentScreen = AppScreen.COURSES },
                    onNavigateToQuiz = { currentScreen = AppScreen.QUIZ },
                    onNavigateToFocus = { currentScreen = AppScreen.FOCUS },
                    onNavigateToTasks = { currentScreen = AppScreen.TASKS },
                    onNavigateToSchema = { currentScreen = AppScreen.SCHEMA },
                    onToggleTask = { studyViewModel.toggleTask(it) },
                    onUpdateProfile = { name, email, course, goal ->
                        studyViewModel.updateProfile(name, email, course, goal)
                    },
                    onIncrementStreak = { studyViewModel.incrementStreak() }
                )

                AppScreen.COURSES -> CoursesScreen(
                    subjects = subjects,
                    chapters = chapters,
                    resources = resources,
                    questions = questions,
                    bookmarks = bookmarks,
                    onAddSubject = { name, color -> studyViewModel.addSubject(name, color) },
                    onDeleteSubject = { studyViewModel.deleteSubject(it) },
                    onAddChapter = { subjId, title -> studyViewModel.addChapter(subjId, title) },
                    onToggleChapter = { studyViewModel.toggleChapterStatus(it) },
                    onDeleteChapter = { studyViewModel.deleteChapter(it) },
                    onAddResource = { chId, title, type, content ->
                        studyViewModel.addResource(chId, title, type, content)
                    },
                    onDeleteResource = { studyViewModel.deleteResource(it) },
                    onAddQuestion = { chId, text, a, b, c, d, correct, exp ->
                        studyViewModel.addQuestion(chId, text, a, b, c, d, correct, exp)
                    },
                    onToggleBookmark = { type, id, title ->
                        studyViewModel.toggleBookmark(type, id, title)
                    },
                    onStartChapterQuiz = { chapterQs, title, chapterId ->
                        studyViewModel.startQuiz(chapterQs, title, chapterId)
                        currentScreen = AppScreen.QUIZ
                    }
                )

                AppScreen.QUIZ -> QuizPracticeScreen(
                    quizState = quizState,
                    subjects = subjects,
                    chapters = chapters,
                    allQuestions = questions,
                    quizHistory = quizHistory,
                    bookmarks = bookmarks,
                    onStartQuiz = { qs, title, chId ->
                        studyViewModel.startQuiz(qs, title, chId)
                    },
                    onSelectOption = { studyViewModel.selectQuizOption(it) },
                    onSubmitAnswer = { studyViewModel.submitCurrentAnswer() },
                    onNextQuestion = { studyViewModel.nextQuizQuestion() },
                    onResetQuiz = { studyViewModel.resetQuiz() },
                    onToggleBookmark = { type, id, title ->
                        studyViewModel.toggleBookmark(type, id, title)
                    },
                    onAddQuestion = { chId, text, a, b, c, d, correct, exp ->
                        studyViewModel.addQuestion(chId, text, a, b, c, d, correct, exp)
                    }
                )

                AppScreen.FOCUS -> FocusTimerScreen(
                    isRunning = pomodoroRunning,
                    secondsLeft = pomodoroSecondsLeft,
                    totalSeconds = pomodoroTotalSeconds,
                    selectedSubjectId = selectedPomodoroSubjectId,
                    subjects = subjects,
                    focusSessions = focusSessions,
                    todayTotalMinutes = todayFocusMinutes ?: 0,
                    onStart = { studyViewModel.startPomodoro() },
                    onPause = { studyViewModel.pausePomodoro() },
                    onReset = { studyViewModel.resetPomodoro() },
                    onSetPreset = { studyViewModel.setPomodoroPreset(it) },
                    onSelectSubject = { studyViewModel.selectPomodoroSubject(it) }
                )

                AppScreen.SCHEMA -> SchemaViewerScreen(
                    userCount = userCount,
                    subjectCount = subjectCount,
                    chapterCount = chapterCount,
                    resourceCount = resourceCount,
                    questionCount = questionCount,
                    quizCount = quizCount,
                    taskCount = taskCount,
                    focusSessionCount = focusSessionCount,
                    bookmarkCount = bookmarkCount,
                    onResetDatabase = { studyViewModel.resetAndSeedDemoData() }
                )

                AppScreen.TASKS -> TasksScreen(
                    tasks = tasks,
                    onAddTask = { title, due, priority -> studyViewModel.addTask(title, due, priority) },
                    onToggleTask = { studyViewModel.toggleTask(it) },
                    onDeleteTask = { studyViewModel.deleteTask(it) }
                )

                AppScreen.BOOKMARKS -> BookmarksScreen(
                    bookmarks = bookmarks,
                    resources = resources,
                    onDeleteBookmark = { studyViewModel.deleteBookmark(it) }
                )
            }
        }
    }
}
