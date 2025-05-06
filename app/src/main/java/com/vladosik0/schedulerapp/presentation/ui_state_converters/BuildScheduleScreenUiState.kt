package com.vladosik0.schedulerapp.presentation.ui_state_converters

import java.time.LocalDate
import java.time.LocalTime

data class BuildScheduleScreenUiState (
    val startDate: LocalDate = LocalDate.now(),
    val finishDate: LocalDate = LocalDate.now().plusDays(4),
    val recommendedDate: LocalDate = LocalDate.now(),
    val temporaryTasks: List<List<TaskUiStateElement>> = emptyList<List<TaskUiStateElement>>(),
    val activityPeriodStart: LocalTime = LocalTime.now(),
    val activityPeriodEnd: LocalTime = LocalTime.now().plusHours(8),
    val desirableTaskPeriodStart: LocalTime = LocalTime.now(),
    val desirableTaskPeriodFinish: LocalTime = LocalTime.now().plusHours(4)
)