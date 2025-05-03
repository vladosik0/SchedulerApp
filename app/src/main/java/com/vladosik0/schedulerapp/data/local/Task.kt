package com.vladosik0.schedulerapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startAt: String,
    val finishAt: String,
    val title: String,
    val description: String?,
    val category: String,
    val duration: Int,
    val difficulty: Int,
    val priority: Int,
    val isNotified: Boolean = false,
    val isDone: Boolean = false
)