package com.vladosik0.schedulerapp.presentation.ui_state_converters

import java.time.LocalDate
import java.time.LocalTime

data class BuildScheduleScreenUiState (
    val startDate: LocalDate = LocalDate.now(),
    val finishDate: LocalDate = LocalDate.now().plusDays(4),
    val recommendedDate: LocalDate = LocalDate.now(),
    val temporaryTasks: MutableMap<TaskUiStateElement, Boolean> = mutableMapOf<TaskUiStateElement, Boolean>(),
    val activityPeriodStart: LocalTime = LocalTime.now(),
    val activityPeriodFinish: LocalTime = LocalTime.now().plusHours(8),
    val desirableExecutionPeriodStart: LocalTime = LocalTime.now(),
    val desirableExecutionPeriodFinish: LocalTime = LocalTime.now().plusHours(4)
)