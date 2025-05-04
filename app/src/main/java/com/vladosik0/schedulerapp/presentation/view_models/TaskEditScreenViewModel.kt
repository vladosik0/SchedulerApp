package com.vladosik0.schedulerapp.presentation.view_models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladosik0.schedulerapp.data.local.repositories.TasksRepository
import com.vladosik0.schedulerapp.domain.enums.Difficulty
import com.vladosik0.schedulerapp.domain.enums.Priority
import com.vladosik0.schedulerapp.domain.parsers.parseDateStringToDate
import com.vladosik0.schedulerapp.domain.parsers.parseDateTimeStringToDate
import com.vladosik0.schedulerapp.domain.parsers.parseDateTimeStringToTime
import com.vladosik0.schedulerapp.presentation.converters.EditTaskScreenUiState
import com.vladosik0.schedulerapp.presentation.converters.toEditTaskScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TaskEditScreenViewModel (
    tasksRepository: TasksRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val taskId: String? = savedStateHandle["taskId"]

    private val date: String? = savedStateHandle["date"]

    private val slot: String? = savedStateHandle["slot"]

    private val _editTaskScreenUiState = MutableStateFlow(EditTaskScreenUiState())
    val editTaskScreenUiState: StateFlow<EditTaskScreenUiState> = _editTaskScreenUiState

    init {
        when {
            taskId != null -> {
                viewModelScope.launch {
                    tasksRepository.getTaskStream(taskId.toInt())
                        .map { it?.toEditTaskScreenUiState() ?: EditTaskScreenUiState() }
                        .collect { _editTaskScreenUiState.value = it }
                }
            }

            date != null -> {
                _editTaskScreenUiState.value = EditTaskScreenUiState(
                    date = parseDateStringToDate(date)
                )
            }

            slot != null -> {
                val startTime = if (LocalDateTime.parse(slot).isBefore(LocalDateTime.now()))
                    LocalTime.now() else parseDateTimeStringToTime(slot)
                _editTaskScreenUiState.value = EditTaskScreenUiState(
                    date = parseDateTimeStringToDate(slot),
                    startTime = startTime,
                    finishTime = startTime.plusMinutes(90)
                )
            }


        }
    }


    val topAppBarTitle: String = if (taskId == null) "Create Task" else "Edit Task"

    fun updateTitle(title: String) {
        if(title.length <= 50) {
            _editTaskScreenUiState.value = _editTaskScreenUiState.value.copy(title = title)
        }
    }

    fun updateDescription(description: String) {
        if(description.length <= 500) {
            _editTaskScreenUiState.value = _editTaskScreenUiState.value.copy(description = description)
        }
    }

    fun updateCategory(category: String) {
        if(category.length <= 50) {
            _editTaskScreenUiState.value = _editTaskScreenUiState.value.copy(category = category)
        }
    }

    fun updateDate(date: LocalDate) {
        _editTaskScreenUiState.value = _editTaskScreenUiState.value.copy(date = date)
    }

    fun updateStartTime(startTime: LocalTime) {
        if(startTime.isBefore(_editTaskScreenUiState.value.finishTime)) {
            _editTaskScreenUiState.value = _editTaskScreenUiState.value.copy(startTime = startTime)

        }
    }

    fun updateFinishTime(finishTime: LocalTime) {
        if(finishTime.isAfter(_editTaskScreenUiState.value.startTime)) {
            _editTaskScreenUiState.value = _editTaskScreenUiState.value.copy(finishTime = finishTime)

        }
    }

    fun updatePriority(priority: Priority) {
        _editTaskScreenUiState.value = _editTaskScreenUiState.value.copy(priority = priority)
    }

    fun updateDifficulty(difficulty: Difficulty) {
        _editTaskScreenUiState.value = _editTaskScreenUiState.value.copy(difficulty = difficulty)
    }

    fun saveTask() {

    }

    //updateIsNotified


}