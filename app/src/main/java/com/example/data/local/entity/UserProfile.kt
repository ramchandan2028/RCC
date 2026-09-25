package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String = "usr_default_01",

    @ColumnInfo(name = "name")
    val name: String = "Convent RCC",

    @ColumnInfo(name = "email")
    val email: String = "convent.rcc@university.edu",

    @ColumnInfo(name = "course_or_class")
    val courseOrClass: String = "BCA",

    @ColumnInfo(name = "daily_study_goal_hours")
    val dailyStudyGoalHours: Double = 4.0,

    @ColumnInfo(name = "streak_count")
    val streakCount: Int = 12,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis() - (14L * 24 * 60 * 60 * 1000)
)
