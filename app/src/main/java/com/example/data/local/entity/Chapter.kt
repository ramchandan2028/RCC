package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chapters",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["subject_id"],
            childColumns = ["subject_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subject_id"])]
)
data class Chapter(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "chapter_id")
    val chapterId: Long = 0,

    @ColumnInfo(name = "subject_id")
    val subjectId: Long,

    @ColumnInfo(name = "chapter_title")
    val chapterTitle: String,

    @ColumnInfo(name = "status")
    val status: String = "pending", // "pending" or "completed"

    @ColumnInfo(name = "order_index")
    val orderIndex: Int = 0
)
