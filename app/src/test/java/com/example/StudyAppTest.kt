package com.example

import com.example.data.local.entity.Chapter
import com.example.data.local.entity.FocusSession
import com.example.data.local.entity.QuizQuestion
import com.example.data.local.entity.QuizResult
import com.example.data.local.entity.StudyResource
import com.example.data.local.entity.StudyTask
import com.example.data.local.entity.Subject
import com.example.data.local.entity.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class StudyAppTest {

    @Test
    fun testUserProfileDefaults() {
        val user = UserProfile(
            userId = "usr_001",
            name = "Test Student",
            email = "student@test.edu",
            courseOrClass = "BCA",
            dailyStudyGoalHours = 3.5,
            streakCount = 5
        )
        assertEquals("usr_001", user.userId)
        assertEquals("BCA", user.courseOrClass)
        assertEquals(3.5, user.dailyStudyGoalHours, 0.001)
        assertEquals(5, user.streakCount)
    }

    @Test
    fun testCourseHierarchyModels() {
        val subject = Subject(subjectId = 1L, subjectName = "Algorithms", userId = "usr_001")
        val chapter = Chapter(chapterId = 10L, subjectId = 1L, chapterTitle = "Dynamic Programming", status = "pending")
        val resource = StudyResource(resourceId = 100L, chapterId = 10L, title = "DP Cheatsheet", type = "formula sheet", fileUrlOrContent = "Memoization O(N)")

        assertEquals(1L, subject.subjectId)
        assertEquals(1L, chapter.subjectId)
        assertEquals(10L, resource.chapterId)
        assertEquals("pending", chapter.status)
        assertEquals("formula sheet", resource.type)
    }

    @Test
    fun testQuizAndProductivityModels() {
        val question = QuizQuestion(
            questionId = 1L,
            chapterId = 10L,
            questionText = "What is memoization?",
            optionA = "Top-down DP caching",
            optionB = "Bottom-up tabulation",
            optionC = "Sorting algorithm",
            optionD = "Graph traversal",
            correctOption = "A",
            explanation = "Memoization stores function call results."
        )
        val quiz = QuizResult(resultId = 1L, userId = "usr_001", quizScore = 5, totalQuestions = 5)
        val task = StudyTask(taskId = 1L, userId = "usr_001", taskTitle = "Solve 5 DP questions", dueDate = 1000L, status = "pending", priority = "high")
        val session = FocusSession(sessionId = 1L, userId = "usr_001", subjectId = 1L, durationMinutes = 25)

        assertEquals("A", question.correctOption)
        assertEquals(5, quiz.quizScore)
        assertEquals("high", task.priority)
        assertEquals(25, session.durationMinutes)
    }
}
