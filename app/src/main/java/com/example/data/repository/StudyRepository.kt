package com.example.data.repository

import com.example.data.local.DatabaseSeeder
import com.example.data.local.dao.StudyDao
import com.example.data.local.entity.Bookmark
import com.example.data.local.entity.Chapter
import com.example.data.local.entity.FocusSession
import com.example.data.local.entity.QuizQuestion
import com.example.data.local.entity.QuizResult
import com.example.data.local.entity.StudyResource
import com.example.data.local.entity.StudyTask
import com.example.data.local.entity.Subject
import com.example.data.local.entity.UserProfile
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class StudyRepository(private val studyDao: StudyDao) {

    // Seed
    suspend fun seedDatabaseIfNeeded() {
        DatabaseSeeder.seedIfNeeded(studyDao)
    }

    suspend fun resetAndReSeed() {
        studyDao.clearBookmarks()
        studyDao.clearFocusSessions()
        studyDao.clearTasks()
        studyDao.clearQuizHistory()
        studyDao.clearQuestions()
        studyDao.clearResources()
        studyDao.clearChapters()
        studyDao.clearSubjects()
        studyDao.clearProfiles()
        DatabaseSeeder.seedAll(studyDao)
    }

    // User Profile
    val userProfile: Flow<UserProfile?> = studyDao.getUserProfile()
    suspend fun updateProfile(profile: UserProfile) = studyDao.insertOrUpdateProfile(profile)
    suspend fun updateDailyGoal(hours: Double) = studyDao.updateDailyGoal("usr_default_01", hours)
    suspend fun updateStreak(streak: Int) = studyDao.updateStreak("usr_default_01", streak)

    // Subjects
    val allSubjects: Flow<List<Subject>> = studyDao.getAllSubjects()
    fun getSubject(subjectId: Long): Flow<Subject?> = studyDao.getSubjectById(subjectId)
    suspend fun addSubject(name: String, colorHex: String = "#2563EB"): Long {
        return studyDao.insertSubject(
            Subject(
                subjectName = name,
                colorHex = colorHex,
                userId = "usr_default_01"
            )
        )
    }
    suspend fun deleteSubject(subject: Subject) = studyDao.deleteSubject(subject)

    // Chapters
    val allChapters: Flow<List<Chapter>> = studyDao.getAllChapters()
    fun getChaptersForSubject(subjectId: Long): Flow<List<Chapter>> =
        studyDao.getChaptersForSubject(subjectId)
    suspend fun addChapter(subjectId: Long, title: String, orderIndex: Int = 0): Long {
        return studyDao.insertChapter(
            Chapter(
                subjectId = subjectId,
                chapterTitle = title,
                status = "pending",
                orderIndex = orderIndex
            )
        )
    }
    suspend fun toggleChapterStatus(chapter: Chapter) {
        val nextStatus = if (chapter.status == "completed") "pending" else "completed"
        studyDao.updateChapterStatus(chapter.chapterId, nextStatus)
    }
    suspend fun deleteChapter(chapter: Chapter) = studyDao.deleteChapter(chapter)

    // Resources
    val allResources: Flow<List<StudyResource>> = studyDao.getAllResources()
    fun getResourcesForChapter(chapterId: Long): Flow<List<StudyResource>> =
        studyDao.getResourcesForChapter(chapterId)
    suspend fun addResource(chapterId: Long, title: String, type: String, content: String): Long {
        return studyDao.insertResource(
            StudyResource(
                chapterId = chapterId,
                title = title,
                type = type,
                fileUrlOrContent = content
            )
        )
    }
    suspend fun deleteResource(resource: StudyResource) = studyDao.deleteResource(resource)

    // Questions
    val allQuestions: Flow<List<QuizQuestion>> = studyDao.getAllQuestions()
    fun getQuestionsForChapter(chapterId: Long): Flow<List<QuizQuestion>> =
        studyDao.getQuestionsForChapter(chapterId)
    fun getQuestionsForSubject(subjectId: Long): Flow<List<QuizQuestion>> =
        studyDao.getQuestionsForSubject(subjectId)
    suspend fun addQuestion(
        chapterId: Long,
        questionText: String,
        optionA: String,
        optionB: String,
        optionC: String,
        optionD: String,
        correctOption: String,
        explanation: String
    ): Long {
        return studyDao.insertQuestion(
            QuizQuestion(
                chapterId = chapterId,
                questionText = questionText,
                optionA = optionA,
                optionB = optionB,
                optionC = optionC,
                optionD = optionD,
                correctOption = correctOption,
                explanation = explanation
            )
        )
    }
    suspend fun deleteQuestion(question: QuizQuestion) = studyDao.deleteQuestion(question)

    // Quiz History
    val quizHistory: Flow<List<QuizResult>> = studyDao.getAllQuizHistory()
    suspend fun recordQuizResult(
        chapterId: Long?,
        subjectName: String,
        score: Int,
        total: Int
    ): Long {
        return studyDao.insertQuizResult(
            QuizResult(
                userId = "usr_default_01",
                chapterId = chapterId,
                subjectName = subjectName,
                quizScore = score,
                totalQuestions = total,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // Study Tasks
    val allTasks: Flow<List<StudyTask>> = studyDao.getAllTasks()
    suspend fun addTask(title: String, dueDate: Long, priority: String = "medium"): Long {
        return studyDao.insertTask(
            StudyTask(
                userId = "usr_default_01",
                taskTitle = title,
                dueDate = dueDate,
                status = "pending",
                priority = priority
            )
        )
    }
    suspend fun toggleTaskStatus(task: StudyTask) {
        val nextStatus = if (task.status == "done") "pending" else "done"
        studyDao.updateTaskStatus(task.taskId, nextStatus)
    }
    suspend fun deleteTask(task: StudyTask) = studyDao.deleteTask(task)

    // Focus Sessions
    val allFocusSessions: Flow<List<FocusSession>> = studyDao.getAllFocusSessions()
    fun getTodayFocusMinutes(): Flow<Int?> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return studyDao.getTodayFocusMinutes(calendar.timeInMillis)
    }
    suspend fun logFocusSession(subjectId: Long, minutes: Int): Long {
        return studyDao.insertFocusSession(
            FocusSession(
                userId = "usr_default_01",
                subjectId = subjectId,
                durationMinutes = minutes,
                date = System.currentTimeMillis()
            )
        )
    }

    // Bookmarks
    val allBookmarks: Flow<List<Bookmark>> = studyDao.getAllBookmarks()
    fun isBookmarked(type: String, itemId: Long): Flow<Boolean> =
        studyDao.isBookmarked(type, itemId)
    suspend fun toggleBookmark(type: String, itemId: Long, title: String) {
        // Toggle by attempting delete or insert
        studyDao.deleteBookmarkByItem(type, itemId)
    }
    suspend fun addBookmark(type: String, itemId: Long, title: String): Long {
        return studyDao.insertBookmark(
            Bookmark(
                userId = "usr_default_01",
                itemType = type,
                itemId = itemId,
                itemTitle = title
            )
        )
    }
    suspend fun removeBookmark(type: String, itemId: Long) {
        studyDao.deleteBookmarkByItem(type, itemId)
    }
    suspend fun deleteBookmark(bookmark: Bookmark) = studyDao.deleteBookmark(bookmark)

    // Inspector
    val userCount: Flow<Int> = studyDao.getUserCount()
    val subjectCount: Flow<Int> = studyDao.getSubjectCount()
    val chapterCount: Flow<Int> = studyDao.getChapterCount()
    val resourceCount: Flow<Int> = studyDao.getResourceCount()
    val questionCount: Flow<Int> = studyDao.getQuestionCount()
    val quizHistoryCount: Flow<Int> = studyDao.getQuizHistoryCount()
    val taskCount: Flow<Int> = studyDao.getTaskCount()
    val focusSessionCount: Flow<Int> = studyDao.getFocusSessionCount()
    val bookmarkCount: Flow<Int> = studyDao.getBookmarkCount()
}
