package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmarks",
    foreignKeys = [
        ForeignKey(
            entity = UserProfile::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["item_type", "item_id"], unique = true)
    ]
)
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "bookmark_id")
    val bookmarkId: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: String = "usr_default_01",

    @ColumnInfo(name = "item_type")
    val itemType: String, // "chapter", "resource", "question"

    @ColumnInfo(name = "item_id")
    val itemId: Long,

    @ColumnInfo(name = "item_title")
    val itemTitle: String = "",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
