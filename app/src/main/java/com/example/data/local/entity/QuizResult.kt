package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quiz_history",
    foreignKeys = [
        ForeignKey(
            entity = UserProfile::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"])]
)
data class QuizResult(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "result_id")
    val resultId: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: String = "usr_default_01",

    @ColumnInfo(name = "chapter_id")
    val chapterId: Long? = null,

    @ColumnInfo(name = "subject_name")
    val subjectName: String = "General Practice",

    @ColumnInfo(name = "quiz_score")
    val quizScore: Int,

    @ColumnInfo(name = "total_questions")
    val totalQuestions: Int,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
