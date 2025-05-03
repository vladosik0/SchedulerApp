package com.vladosik0.schedulerapp.presentation

import com.vladosik0.schedulerapp.data.local.Task
import com.vladosik0.schedulerapp.domain.enums.Difficulty
import com.vladosik0.schedulerapp.domain.enums.Priority

data class TaskUiStateElement (
    val id: Int = 0,
    val startAt: String = "",
    val finishAt: String = "",
    val title: String = "",
    val description: String? = null,
    val category: String = "",
    val duration: Int = 0,
    val difficulty: Difficulty = Difficulty.NORMAL,
    val priority: Priority = Priority.LOW,
    val isNotified: Boolean = false,
    val isDone: Boolean = false
)

fun TaskUiStateElement.toTask(): Task = Task(
    id = id,
    startAt = startAt,
    finishAt = finishAt,
    title = title,
    description = description,
    category = category,
    duration = duration,
    difficulty = if(difficulty == Difficulty.NORMAL) 1 else 2,
    priority = if(priority == Priority.LOW) 1 else 2,
    isNotified = isNotified,
    isDone = isDone
)

fun Task.toTaskUiState(actionEnabled: Boolean = false): TaskUiStateElement = TaskUiStateElement(
    id = id,
    startAt = startAt,
    finishAt = finishAt,
    title = title,
    description = description,
    category = category,
    duration = duration,
    difficulty = if(difficulty == 1) Difficulty.NORMAL else Difficulty.HIGH,
    priority = if(priority == 1) Priority.LOW else Priority.HIGH,
    isNotified = isNotified,
    isDone = isDone
)