package com.vladosik0.schedulerapp.presentation.ui_state_converters

import java.time.LocalDate
import java.time.LocalTime

data class BuildScheduleScreenUiState (
    val startDate: LocalDate = LocalDate.now(),
    val finishDate: LocalDate = LocalDate.now().plusDays(4),
    val recommendedDate: LocalDate = LocalDate.now(),
    val temporaryTasks: MutableList<TaskUiStateElement> = mutableListOf<TaskUiStateElement>(),
    val activityPeriodStart: LocalTime = LocalTime.now(),
    val activityPeriodFinish: LocalTime = LocalTime.now().plusHours(8),
    val desirableExecutionPeriodStart: LocalTime = LocalTime.now(),
    val desirableExecutionPeriodFinish: LocalTime = LocalTime.now().plusHours(4),
    val considerDesirableExecutionPeriod: Boolean = false,
    val newTaskDurationInMinutes: Int = 30
)