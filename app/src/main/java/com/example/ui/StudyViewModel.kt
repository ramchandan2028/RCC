package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.Bookmark
import com.example.data.local.entity.Chapter
import com.example.data.local.entity.FocusSession
import com.example.data.local.entity.QuizQuestion
import com.example.data.local.entity.QuizResult
import com.example.data.local.entity.StudyResource
import com.example.data.local.entity.StudyTask
import com.example.data.local.entity.Subject
import com.example.data.local.entity.UserProfile
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActiveQuizState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: String? = null,
    val isSubmitted: Boolean = false,
    val isCorrect: Boolean = false,
    val userAnswers: MutableMap<Int, String> = mutableMapOf(),
    val score: Int = 0,
    val isFinished: Boolean = false,
    val subjectTitle: String = "General Practice",
    val chapterId: Long? = null
)

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudyRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = StudyRepository(db.studyDao())
        viewModelScope.launch {
            repository.seedDatabaseIfNeeded()
        }
    }

    // --- Flows ---
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val subjects: StateFlow<List<Subject>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chapters: StateFlow<List<Chapter>> = repository.allChapters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val resources: StateFlow<List<StudyResource>> = repository.allResources
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val questions: StateFlow<List<QuizQuestion>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizHistory: StateFlow<List<QuizResult>> = repository.quizHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<StudyTask>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val focusSessions: StateFlow<List<FocusSession>> = repository.allFocusSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayFocusMinutes: StateFlow<Int?> = repository.getTodayFocusMinutes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val bookmarks: StateFlow<List<Bookmark>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Inspector counts
    val userCount: StateFlow<Int> = repository.userCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
    val subjectCount: StateFlow<Int> = repository.subjectCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val chapterCount: StateFlow<Int> = repository.chapterCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val resourceCount: StateFlow<Int> = repository.resourceCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val questionCount: StateFlow<Int> = repository.questionCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val quizCount: StateFlow<Int> = repository.quizHistoryCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val taskCount: StateFlow<Int> = repository.taskCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val focusSessionCount: StateFlow<Int> = repository.focusSessionCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val bookmarkCount: StateFlow<Int> = repository.bookmarkCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // --- Active Quiz State ---
    private val _quizState = MutableStateFlow(ActiveQuizState())
    val quizState: StateFlow<ActiveQuizState> = _quizState.asStateFlow()

    // --- Pomodoro Timer State ---
    private val _pomodoroRunning = MutableStateFlow(false)
    val pomodoroRunning: StateFlow<Boolean> = _pomodoroRunning.asStateFlow()

    private val _pomodoroSecondsLeft = MutableStateFlow(25 * 60)
    val pomodoroSecondsLeft: StateFlow<Int> = _pomodoroSecondsLeft.asStateFlow()

    private val _pomodoroTotalSeconds = MutableStateFlow(25 * 60)
    val pomodoroTotalSeconds: StateFlow<Int> = _pomodoroTotalSeconds.asStateFlow()

    private val _selectedPomodoroSubjectId = MutableStateFlow<Long?>(null)
    val selectedPomodoroSubjectId: StateFlow<Long?> = _selectedPomodoroSubjectId.asStateFlow()

    private var pomodoroJob: Job? = null

    // --- Toast / Snackbar Message ---
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun clearUserMessage() {
        _userMessage.value = null
    }

    // --- Subject & Chapter Actions ---
    fun addSubject(name: String, colorHex: String) {
        viewModelScope.launch {
            repository.addSubject(name, colorHex)
            _userMessage.value = "Added subject '$name'"
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
            _userMessage.value = "Deleted subject '${subject.subjectName}'"
        }
    }

    fun addChapter(subjectId: Long, title: String) {
        viewModelScope.launch {
            repository.addChapter(subjectId, title)
            _userMessage.value = "Added chapter '$title'"
        }
    }

    fun toggleChapterStatus(chapter: Chapter) {
        viewModelScope.launch {
            repository.toggleChapterStatus(chapter)
        }
    }

    fun deleteChapter(chapter: Chapter) {
        viewModelScope.launch {
            repository.deleteChapter(chapter)
            _userMessage.value = "Deleted chapter '${chapter.chapterTitle}'"
        }
    }

    // --- Resource Actions ---
    fun addResource(chapterId: Long, title: String, type: String, content: String) {
        viewModelScope.launch {
            repository.addResource(chapterId, title, type, content)
            _userMessage.value = "Added resource '$title'"
        }
    }

    fun deleteResource(resource: StudyResource) {
        viewModelScope.launch {
            repository.deleteResource(resource)
            _userMessage.value = "Deleted resource '${resource.title}'"
        }
    }

    // --- Question Actions ---
    fun addQuestion(
        chapterId: Long,
        text: String,
        optA: String,
        optB: String,
        optC: String,
        optD: String,
        correct: String,
        explanation: String
    ) {
        viewModelScope.launch {
            repository.addQuestion(chapterId, text, optA, optB, optC, optD, correct, explanation)
            _userMessage.value = "Added practice question"
        }
    }

    // --- Quiz Engine Actions ---
    fun startQuiz(filteredQuestions: List<QuizQuestion>, title: String, chapterId: Long? = null) {
        if (filteredQuestions.isEmpty()) {
            _userMessage.value = "No questions available for this selection."
            return
        }
        _quizState.value = ActiveQuizState(
            questions = filteredQuestions.shuffled(),
            currentIndex = 0,
            selectedOption = null,
            isSubmitted = false,
            score = 0,
            isFinished = false,
            subjectTitle = title,
            chapterId = chapterId
        )
    }

    fun selectQuizOption(option: String) {
        val current = _quizState.value
        if (!current.isSubmitted && !current.isFinished) {
            _quizState.value = current.copy(selectedOption = option)
        }
    }

    fun submitCurrentAnswer() {
        val current = _quizState.value
        if (current.selectedOption == null || current.isSubmitted) return

        val currentQ = current.questions.getOrNull(current.currentIndex) ?: return
        val isCorrect = current.selectedOption == currentQ.correctOption
        val newScore = if (isCorrect) current.score + 1 else current.score

        _quizState.value = current.copy(
            isSubmitted = true,
            isCorrect = isCorrect,
            score = newScore
        )
    }

    fun nextQuizQuestion() {
        val current = _quizState.value
        if (current.currentIndex + 1 < current.questions.size) {
            _quizState.value = current.copy(
                currentIndex = current.currentIndex + 1,
                selectedOption = null,
                isSubmitted = false,
                isCorrect = false
            )
        } else {
            // Quiz finished!
            _quizState.value = current.copy(isFinished = true)
            // Log to quiz history
            viewModelScope.launch {
                repository.recordQuizResult(
                    chapterId = current.chapterId,
                    subjectName = current.subjectTitle,
                    score = current.score,
                    total = current.questions.size
                )
            }
        }
    }

    fun resetQuiz() {
        _quizState.value = ActiveQuizState()
    }

    // --- Pomodoro Timer Actions ---
    fun setPomodoroPreset(minutes: Int) {
        if (_pomodoroRunning.value) pausePomodoro()
        _pomodoroTotalSeconds.value = minutes * 60
        _pomodoroSecondsLeft.value = minutes * 60
    }

    fun selectPomodoroSubject(subjectId: Long?) {
        _selectedPomodoroSubjectId.value = subjectId
    }

    fun startPomodoro() {
        if (_pomodoroRunning.value) return
        _pomodoroRunning.value = true
        pomodoroJob?.cancel()
        pomodoroJob = viewModelScope.launch {
            while (_pomodoroSecondsLeft.value > 0 && _pomodoroRunning.value) {
                delay(1000)
                _pomodoroSecondsLeft.value -= 1
            }
            if (_pomodoroSecondsLeft.value <= 0) {
                _pomodoroRunning.value = false
                completePomodoroSession()
            }
        }
    }

    fun pausePomodoro() {
        _pomodoroRunning.value = false
        pomodoroJob?.cancel()
    }

    fun resetPomodoro() {
        pausePomodoro()
        _pomodoroSecondsLeft.value = _pomodoroTotalSeconds.value
    }

    private fun completePomodoroSession() {
        val durationMins = _pomodoroTotalSeconds.value / 60
        val subjectId = _selectedPomodoroSubjectId.value ?: 1L
        viewModelScope.launch {
            repository.logFocusSession(subjectId, durationMins)
            _userMessage.value = "Great work! Completed $durationMins min focus session."
            _pomodoroSecondsLeft.value = _pomodoroTotalSeconds.value
        }
    }

    // --- Task Actions ---
    fun addTask(title: String, dueDate: Long, priority: String) {
        viewModelScope.launch {
            repository.addTask(title, dueDate, priority)
            _userMessage.value = "Task created"
        }
    }

    fun toggleTask(task: StudyTask) {
        viewModelScope.launch {
            repository.toggleTaskStatus(task)
        }
    }

    fun deleteTask(task: StudyTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
            _userMessage.value = "Task deleted"
        }
    }

    // --- Bookmark Actions ---
    fun toggleBookmark(type: String, itemId: Long, title: String) {
        viewModelScope.launch {
            val exists = bookmarks.value.any { it.itemType == type && it.itemId == itemId }
            if (exists) {
                repository.removeBookmark(type, itemId)
                _userMessage.value = "Removed from bookmarks"
            } else {
                repository.addBookmark(type, itemId, title)
                _userMessage.value = "Saved to bookmarks"
            }
        }
    }

    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            repository.deleteBookmark(bookmark)
            _userMessage.value = "Bookmark deleted"
        }
    }

    // --- User Profile & Database Maintenance ---
    fun updateProfile(name: String, email: String, course: String, goalHours: Double) {
        val current = userProfile.value ?: UserProfile()
        viewModelScope.launch {
            repository.updateProfile(
                current.copy(
                    name = name,
                    email = email,
                    courseOrClass = course,
                    dailyStudyGoalHours = goalHours
                )
            )
            _userMessage.value = "Profile updated"
        }
    }

    fun incrementStreak() {
        val current = userProfile.value ?: return
        viewModelScope.launch {
            repository.updateStreak(current.streakCount + 1)
            _userMessage.value = "Streak increased to ${current.streakCount + 1} days! 🔥"
        }
    }

    fun resetAndSeedDemoData() {
        viewModelScope.launch {
            repository.resetAndReSeed()
            _userMessage.value = "Database re-seeded with demo data!"
        }
    }
}
