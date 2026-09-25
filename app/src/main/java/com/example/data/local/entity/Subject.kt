package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subjects",
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
data class Subject(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "subject_id")
    val subjectId: Long = 0,

    @ColumnInfo(name = "subject_name")
    val subjectName: String,

    @ColumnInfo(name = "user_id")
    val userId: String = "usr_default_01",

    @ColumnInfo(name = "color_hex")
    val colorHex: String = "#3B82F6",

    @ColumnInfo(name = "icon_name")
    val iconName: String = "MenuBook"
)
