package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_tasks",
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
data class StudyTask(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_id")
    val taskId: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: String = "usr_default_01",

    @ColumnInfo(name = "task_title")
    val taskTitle: String,

    @ColumnInfo(name = "due_date")
    val dueDate: Long, // timestamp in millis

    @ColumnInfo(name = "status")
    val status: String = "pending", // "pending" or "done"

    @ColumnInfo(name = "priority")
    val priority: String = "medium" // "high", "medium", "low"
)
