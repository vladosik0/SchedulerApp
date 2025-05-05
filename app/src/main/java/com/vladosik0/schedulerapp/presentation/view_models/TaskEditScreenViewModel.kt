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
import com.vladosik0.schedulerapp.presentation.converters.TaskEditScreenUiState
import com.vladosik0.schedulerapp.presentation.converters.toEditTaskScreenUiState
import com.vladosik0.schedulerapp.presentation.converters.toTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TaskEditScreenViewModel (
    private val tasksRepository: TasksRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val taskId: Int? = savedStateHandle["taskId"]

    private val date: String? = savedStateHandle["date"]

    private val slot: String? = savedStateHandle["slot"]

    private val _taskEditScreenUiState = MutableStateFlow(TaskEditScreenUiState())
    val taskEditScreenUiState: StateFlow<TaskEditScreenUiState> = _taskEditScreenUiState

    init {
        when {
            taskId != null -> {
                viewModelScope.launch {
                    tasksRepository.getTaskStream(taskId)
                        .map { it?.toEditTaskScreenUiState() ?: TaskEditScreenUiState() }
                        .collect { _taskEditScreenUiState.value = it }
                }
            }

            date != null -> {
                _taskEditScreenUiState.value = TaskEditScreenUiState(
                    date = parseDateStringToDate(date)
                )
            }

            slot != null -> {
                val startTime = if (LocalDateTime.parse(slot).isBefore(LocalDateTime.now()))
                    LocalTime.now() else parseDateTimeStringToTime(slot)
                _taskEditScreenUiState.value = TaskEditScreenUiState(
                    date = parseDateTimeStringToDate(slot),
                    startTime = startTime,
                    finishTime = startTime.plusMinutes(90)
                )
            }
        }
    }

    val topAppBarTitle: String = if (taskId == null) "Create Task" else "Edit Task"

    private val _startTimeErrorMessage = MutableStateFlow("")
    val startTimeErrorMessage: StateFlow<String> = _startTimeErrorMessage

    private val _finishTimeErrorMessage = MutableStateFlow("")
    val finishTimeErrorMessage: StateFlow<String> = _finishTimeErrorMessage

    private val _saveTaskErrorMessage = MutableStateFlow("")
    val saveTaskErrorMessage: StateFlow<String> = _saveTaskErrorMessage

    private val _isTaskValid = MutableStateFlow(false)
    val isTaskValid: StateFlow<Boolean> = _isTaskValid

    fun updateTitle(title: String) {
        _taskEditScreenUiState.value = _taskEditScreenUiState.value.copy(title = title)
        checkTaskValidation()
    }

    fun updateDescription(description: String) {
        _taskEditScreenUiState.value = _taskEditScreenUiState.value.copy(description = description)
        checkTaskValidation()
    }

    fun updateCategory(category: String) {
        _taskEditScreenUiState.value = _taskEditScreenUiState.value.copy(category = category)
        checkTaskValidation()
    }

    fun updateDate(date: LocalDate) {
        _taskEditScreenUiState.value = _taskEditScreenUiState.value.copy(date = date)
        checkTaskValidation()
    }

    fun updateStartTime(startTime: LocalTime) {
        _taskEditScreenUiState.value = _taskEditScreenUiState.value.copy(startTime = startTime)
        checkTaskValidation()
    }

    fun updateFinishTime(finishTime: LocalTime) {
        _taskEditScreenUiState.value = _taskEditScreenUiState.value.copy(finishTime = finishTime)
        checkTaskValidation()
    }

    fun updatePriority(priority: Priority) {
        _taskEditScreenUiState.value = _taskEditScreenUiState.value.copy(priority = priority)
    }

    fun updateDifficulty(difficulty: Difficulty) {
        _taskEditScreenUiState.value = _taskEditScreenUiState.value.copy(difficulty = difficulty)
    }

    private fun checkTaskValidation() {
        viewModelScope.launch {

            var isValid = true

            val title = _taskEditScreenUiState.value.title
            if((title.length > 50 || title.isBlank())) {
                isValid = false
            }

            val description = _taskEditScreenUiState.value.description
            if((description.length > 250 || description.isBlank())) {
                isValid = false
            }

            val category = _taskEditScreenUiState.value.category
            if((category.length > 50 || category.isBlank())) {
                isValid = false
            }

            val startTime = _taskEditScreenUiState.value.startTime
            val finishTime = _taskEditScreenUiState.value.finishTime
            if(startTime.isAfter(finishTime)) {
                isValid = false
                _startTimeErrorMessage.value = "Start Time must be before Finish Time"
            } else {
                _startTimeErrorMessage.value = ""
            }
            if(finishTime.isBefore(startTime)) {
                isValid = false
                _finishTimeErrorMessage.value = "Finish Time must be after Start Time"
            } else {
                _finishTimeErrorMessage.value = ""
            }

            val newTaskStartAt = _taskEditScreenUiState.value.date.atTime(_taskEditScreenUiState.value.startTime)
            val newTaskFinishAt = _taskEditScreenUiState.value.date.atTime(_taskEditScreenUiState.value.finishTime)
            val tasks = tasksRepository.getTasksByDate(_taskEditScreenUiState.value.date.toString()).first()

            val hasOverlap = tasks.any { task ->
                val taskStartAt = LocalDateTime.parse(task.startAt)
                val taskFinishAt = LocalDateTime.parse(task.finishAt)
                newTaskStartAt < taskFinishAt && newTaskFinishAt >= taskStartAt
            }

            if(hasOverlap) {
                _saveTaskErrorMessage.value = "There are already tasks in this time interval!"
                isValid = false
            } else {
                _saveTaskErrorMessage.value = ""
            }
            _isTaskValid.value = isValid
        }
    }

    fun saveTask() {
        viewModelScope.launch {
            if (taskId != null) {
                tasksRepository.updateTask(_taskEditScreenUiState.value.toTask(taskId.toInt()))
            } else {
                tasksRepository.insertTask(_taskEditScreenUiState.value.toTask())
            }
        }
    }

    //updateIsNotified


}