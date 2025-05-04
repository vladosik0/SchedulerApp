package com.vladosik0.schedulerapp.presentation.converters

import com.vladosik0.schedulerapp.data.local.Task
import com.vladosik0.schedulerapp.domain.enums.Difficulty
import com.vladosik0.schedulerapp.domain.enums.Priority
import com.vladosik0.schedulerapp.domain.parsers.parseDateTimeStringToDate
import com.vladosik0.schedulerapp.domain.parsers.parseDateTimeStringToTime
import java.time.LocalDate
import java.time.LocalTime

data class EditTaskScreenUiState(
    val date: LocalDate = LocalDate.now(),
    val startTime: LocalTime = LocalTime.now(),
    val finishTime: LocalTime = LocalTime.now().plusMinutes(30),
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val difficulty: Difficulty = Difficulty.NORMAL,
    val priority: Priority = Priority.LOW
)

fun EditTaskScreenUiState.toTask(id: Int): Task = Task(
    id = id,
    startAt = "${date.atTime(startTime)}",
    finishAt = "${date.atTime(finishTime)}",
    title = title,
    description = description,
    category = category,
    difficulty = if(difficulty == Difficulty.NORMAL) 1 else 2,
    priority = if(priority == Priority.LOW) 1 else 2
)

fun Task.toEditTaskScreenUiState(): EditTaskScreenUiState = EditTaskScreenUiState(
    date = parseDateTimeStringToDate(startAt),
    startTime = parseDateTimeStringToTime(startAt),
    finishTime = parseDateTimeStringToTime(finishAt),
    title = title,
    description = description.toString(),
    category = category,
    difficulty = if(difficulty == 1) Difficulty.NORMAL else Difficulty.HIGH,
    priority = if(priority == 1) Priority.LOW else Priority.HIGH
)