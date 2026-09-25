package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "resources",
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
data class StudyResource(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "resource_id")
    val resourceId: Long = 0,

    @ColumnInfo(name = "chapter_id")
    val chapterId: Long,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "type")
    val type: String, // "PDF", "text note", "video link", "formula sheet"

    @ColumnInfo(name = "file_url_or_content")
    val fileUrlOrContent: String
)
