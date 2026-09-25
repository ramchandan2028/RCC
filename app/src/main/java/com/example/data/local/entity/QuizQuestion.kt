package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = Chapter::class,
            parentColumns = ["chapter_id"],
            childColumns = ["chapter_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["chapter_id"])]
)
data class QuizQuestion(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "question_id")
    val questionId: Long = 0,

    @ColumnInfo(name = "chapter_id")
    val chapterId: Long,

    @ColumnInfo(name = "question_text")
    val questionText: String,

    @ColumnInfo(name = "option_a")
    val optionA: String,

    @ColumnInfo(name = "option_b")
    val optionB: String,

    @ColumnInfo(name = "option_c")
    val optionC: String,

    @ColumnInfo(name = "option_d")
    val optionD: String,

    @ColumnInfo(name = "correct_option")
    val correctOption: String, // "A", "B", "C", "D"

    @ColumnInfo(name = "explanation")
    val explanation: String
)
