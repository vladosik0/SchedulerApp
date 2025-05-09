package com.vladosik0.schedulerapp.presentation.ui_state_converters

import com.vladosik0.schedulerapp.domain.enums.Difficulty
import com.vladosik0.schedulerapp.domain.enums.Priority
import java.time.LocalDate
import java.time.LocalTime

data class BuildScheduleScreenUiState (
    val newTaskId: Int = 0,
    val newTaskTitle: String = "",
    val newTaskCategory: String = "",
    val newTaskDescription: String = "",
    val newTaskPriority: Priority = Priority.LOW,
    val newTaskDifficulty: Difficulty = Difficulty.NORMAL,
    val startDate: LocalDate = LocalDate.now(),
    val finishDate: LocalDate = LocalDate.now().plusDays(4),
    val isRecommendedDateLoading: Boolean = false,
    val recommendedDate: LocalDate = LocalDate.now(),
    val temporaryTasks: MutableList<TaskUiStateElement> = mutableListOf<TaskUiStateElement>(),
    val activityPeriodStart: LocalTime = LocalTime.now(),
    val activityPeriodFinish: LocalTime = LocalTime.now().plusHours(8),
    val desirableExecutionPeriodStart: LocalTime = LocalTime.now(),
    val desirableExecutionPeriodFinish: LocalTime = LocalTime.now().plusHours(8),
    val considerDesirableExecutionPeriod: Boolean = false,
    val newTaskDurationInMinutes: Int = 30,
    val isTaskDurationMinutesValid: Boolean = true
)