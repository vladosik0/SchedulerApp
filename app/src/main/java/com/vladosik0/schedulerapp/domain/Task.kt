package com.vladosik0.schedulerapp.domain

import com.vladosik0.schedulerapp.domain.enums.Difficulty
import com.vladosik0.schedulerapp.domain.enums.Priority

data class Task(
    val id: Int,
    val startAt: String,
    val finishAt: String,
    val title: String,
    val description: String?,
    val category: String,
    val duration: Int,
    val difficulty: Difficulty,
    val priority: Priority,
    val isNotified: Boolean = false,
    val isDone: Boolean = false
)
