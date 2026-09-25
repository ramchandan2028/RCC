package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface StudyDao {

    // --- User Profile ---
    @Query("SELECT * FROM user_profiles WHERE user_id = :userId LIMIT 1")
    fun getUserProfile(userId: String = "usr_default_01"): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles LIMIT 1")
    suspend fun getUserProfileDirect(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    @Query("UPDATE user_profiles SET streak_count = :streak WHERE user_id = :userId")
    suspend fun updateStreak(userId: String, streak: Int)

    @Query("UPDATE user_profiles SET daily_study_goal_hours = :goal WHERE user_id = :userId")
    suspend fun updateDailyGoal(userId: String, goal: Double)

    // --- Subjects ---
    @Query("SELECT * FROM subjects ORDER BY subject_id ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE subject_id = :subjectId LIMIT 1")
    fun getSubjectById(subjectId: Long): Flow<Subject?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<Subject>)

    @Delete
    suspend fun deleteSubject(subject: Subject)

    // --- Chapters ---
    @Query("SELECT * FROM chapters ORDER BY order_index ASC, chapter_id ASC")
    fun getAllChapters(): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE subject_id = :subjectId ORDER BY order_index ASC")
    fun getChaptersForSubject(subjectId: Long): Flow<List<Chapter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: Chapter): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<Chapter>)

    @Query("UPDATE chapters SET status = :status WHERE chapter_id = :chapterId")
    suspend fun updateChapterStatus(chapterId: Long, status: String)

    @Delete
    suspend fun deleteChapter(chapter: Chapter)

    // --- Resources ---
    @Query("SELECT * FROM resources ORDER BY resource_id ASC")
    fun getAllResources(): Flow<List<StudyResource>>

    @Query("SELECT * FROM resources WHERE chapter_id = :chapterId")
    fun getResourcesForChapter(chapterId: Long): Flow<List<StudyResource>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: StudyResource): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResources(resources: List<StudyResource>)

    @Delete
    suspend fun deleteResource(resource: StudyResource)

    // --- Questions ---
    @Query("SELECT * FROM questions ORDER BY question_id ASC")
    fun getAllQuestions(): Flow<List<QuizQuestion>>

    @Query("SELECT * FROM questions WHERE chapter_id = :chapterId")
    fun getQuestionsForChapter(chapterId: Long): Flow<List<QuizQuestion>>

    @Query("""
        SELECT q.* FROM questions q 
        INNER JOIN chapters c ON q.chapter_id = c.chapter_id 
        WHERE c.subject_id = :subjectId
    """)
    fun getQuestionsForSubject(subjectId: Long): Flow<List<QuizQuestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuizQuestion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuizQuestion>)

    @Delete
    suspend fun deleteQuestion(question: QuizQuestion)

    // --- Quiz History ---
    @Query("SELECT * FROM quiz_history ORDER BY timestamp DESC")
    fun getAllQuizHistory(): Flow<List<QuizResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResult): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResults(results: List<QuizResult>)

    // --- Study Tasks ---
    @Query("SELECT * FROM study_tasks ORDER BY status ASC, due_date ASC")
    fun getAllTasks(): Flow<List<StudyTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudyTask): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<StudyTask>)

    @Query("UPDATE study_tasks SET status = :status WHERE task_id = :taskId")
    suspend fun updateTaskStatus(taskId: Long, status: String)

    @Delete
    suspend fun deleteTask(task: StudyTask)

    // --- Focus Sessions ---
    @Query("SELECT * FROM focus_sessions ORDER BY date DESC")
    fun getAllFocusSessions(): Flow<List<FocusSession>>

    @Query("SELECT SUM(duration_minutes) FROM focus_sessions WHERE date >= :startOfDayTimestamp")
    fun getTodayFocusMinutes(startOfDayTimestamp: Long): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSession): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSessions(sessions: List<FocusSession>)

    // --- Bookmarks ---
    @Query("SELECT * FROM bookmarks ORDER BY created_at DESC")
    fun getAllBookmarks(): Flow<List<Bookmark>>

    @Query("SELECT COUNT(*) > 0 FROM bookmarks WHERE item_type = :itemType AND item_id = :itemId")
    fun isBookmarked(itemType: String, itemId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: Bookmark): Long

    @Query("DELETE FROM bookmarks WHERE item_type = :itemType AND item_id = :itemId")
    suspend fun deleteBookmarkByItem(itemType: String, itemId: Long)

    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)

    // --- Inspector Counts ---
    @Query("SELECT COUNT(*) FROM user_profiles")
    fun getUserCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM subjects")
    fun getSubjectCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM chapters")
    fun getChapterCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM resources")
    fun getResourceCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM questions")
    fun getQuestionCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM quiz_history")
    fun getQuizHistoryCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM study_tasks")
    fun getTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM focus_sessions")
    fun getFocusSessionCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM bookmarks")
    fun getBookmarkCount(): Flow<Int>

    // Clear all for demo re-seeding
    @Query("DELETE FROM bookmarks")
    suspend fun clearBookmarks()
    @Query("DELETE FROM focus_sessions")
    suspend fun clearFocusSessions()
    @Query("DELETE FROM study_tasks")
    suspend fun clearTasks()
    @Query("DELETE FROM quiz_history")
    suspend fun clearQuizHistory()
    @Query("DELETE FROM questions")
    suspend fun clearQuestions()
    @Query("DELETE FROM resources")
    suspend fun clearResources()
    @Query("DELETE FROM chapters")
    suspend fun clearChapters()
    @Query("DELETE FROM subjects")
    suspend fun clearSubjects()
    @Query("DELETE FROM user_profiles")
    suspend fun clearProfiles()
}
