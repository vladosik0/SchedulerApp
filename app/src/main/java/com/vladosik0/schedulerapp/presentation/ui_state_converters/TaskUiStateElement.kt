package com.vladosik0.schedulerapp.presentation.ui_state_converters

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
    difficulty = if(difficulty == Difficulty.NORMAL) 1 else 2,
    priority = if(priority == Priority.LOW) 1 else 2,
    isNotified = isNotified,
    isDone = isDone
)

fun Task.toTaskUiStateElement(): TaskUiStateElement = TaskUiStateElement(
    id = id,
    startAt = startAt,
    finishAt = finishAt,
    title = title,
    description = description,
    category = category,
    difficulty = if(difficulty == 1) Difficulty.NORMAL else Difficulty.HIGH,
    priority = if(priority == 1) Priority.LOW else Priority.HIGH,
    isNotified = isNotified,
    isDone = isDone
)