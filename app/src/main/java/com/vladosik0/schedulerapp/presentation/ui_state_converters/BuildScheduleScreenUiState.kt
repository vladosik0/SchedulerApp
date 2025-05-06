package com.vladosik0.schedulerapp.presentation.ui_state_converters

import com.vladosik0.schedulerapp.data.local.Task
import java.time.LocalDate
import java.time.LocalTime

data class BuildScheduleScreenUiState (
    val startDate: LocalDate = LocalDate.now(),
    val finishDate: LocalDate = LocalDate.now().plusDays(4),
    val dateRange: List<LocalDate> = emptyList<LocalDate>(),
    val tasks: List<List<Task>> = emptyList<List<Task>>(),
    val temporaryTasks: List<List<TaskUiStateElement>> = emptyList<List<TaskUiStateElement>>(),
    val activityPeriodStart: LocalTime = LocalTime.now(),
    val activityPeriodEnd: LocalTime = LocalTime.now().plusHours(3)
)