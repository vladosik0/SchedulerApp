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
import com.vladosik0.schedulerapp.presentation.ui_state_converters.TaskEditScreenUiState
import com.vladosik0.schedulerapp.presentation.ui_state_converters.toEditTaskScreenUiState
import com.vladosik0.schedulerapp.presentation.ui_state_converters.toTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
                viewModelScope.launch(Dispatchers.IO) {
                    tasksRepository.getTaskStream(taskId)
                        .map { it?.toEditTaskScreenUiState() ?: TaskEditScreenUiState() }
                        .collect {
                            delay(1000)
                            _taskEditScreenUiState.value = it.copy(isLoading = false)
                        }
                }
            }

            date != null -> {
                viewModelScope.launch {
                    delay(1000)
                    _taskEditScreenUiState.value = TaskEditScreenUiState(
                        date = parseDateStringToDate(date), isLoading = false
                    )
                }
            }

            slot != null -> {
                viewModelScope.launch {
                    delay(1000)
                    val startTime = if (LocalDateTime.parse(slot)
                            .isBefore(LocalDateTime.now())
                    ) LocalTime.now() else parseDateTimeStringToTime(slot)
                    _taskEditScreenUiState.value = TaskEditScreenUiState(
                        date = parseDateTimeStringToDate(slot),
                        startTime = startTime,
                        finishTime = startTime.plusMinutes(90),
                        isLoading = false
                    )
                }
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

    private val _isTaskValid = MutableStateFlow(taskId != null)
    val isTaskValid: StateFlow<Boolean> = _isTaskValid

    private val _areTextFieldsValid = MutableStateFlow(false)
    val areTextFieldsValid: StateFlow<Boolean> = _areTextFieldsValid

    fun updateTitle(title: String) {
        _taskEditScreenUiState.value = _taskEditScreenUiState.value.copy(title = title)
        validateTextFields()
    }

    fun updateDescription(description: String) {
        _taskEditScreenUiState.value = _taskEditScreenUiState.value.copy(description = description)
        validateTextFields()
    }

    fun updateCategory(category: String) {
        _taskEditScreenUiState.value = _taskEditScreenUiState.value.copy(category = category)
        validateTextFields()
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

    private fun validateTextFields() {

        val title = _taskEditScreenUiState.value.title
        _areTextFieldsValid.value = !(title.length > 50 || title.isBlank())

        val description = _taskEditScreenUiState.value.description
        _areTextFieldsValid.value = description.length <= 250

        val category = _taskEditScreenUiState.value.category
        _areTextFieldsValid.value = !(category.length > 50 || category.isBlank())
    }

    private fun checkTaskValidation() {

        var isValid = _areTextFieldsValid.value

        val startTime = _taskEditScreenUiState.value.startTime
        val finishTime = _taskEditScreenUiState.value.finishTime
        if (startTime.isAfter(finishTime) || startTime == finishTime) {
            isValid = false
            _startTimeErrorMessage.value = "Start Time must be before Finish Time"
        } else {
            _startTimeErrorMessage.value = ""
        }
        if (finishTime.isBefore(startTime) || finishTime == startTime) {
            isValid = false
            _finishTimeErrorMessage.value = "Finish Time must be after Start Time"
        } else {
            _finishTimeErrorMessage.value = ""
        }

        val newTaskStartAt =
            _taskEditScreenUiState.value.date.atTime(_taskEditScreenUiState.value.startTime)
        val newTaskFinishAt =
            _taskEditScreenUiState.value.date.atTime(_taskEditScreenUiState.value.finishTime)

        viewModelScope.launch(Dispatchers.IO) {
            val tasks =
                tasksRepository.getTasksByDate(_taskEditScreenUiState.value.date.toString()).first()

            val hasOverlap = tasks.any { task ->
                val taskStartAt = LocalDateTime.parse(task.startAt)
                val taskFinishAt = LocalDateTime.parse(task.finishAt)
                newTaskStartAt < taskFinishAt && newTaskFinishAt >= taskStartAt && task.id != taskId
            }

            if (hasOverlap) {
                _saveTaskErrorMessage.value = "There are already tasks in this time interval!"
                isValid = false
            } else {
                _saveTaskErrorMessage.value = ""
            }
            _isTaskValid.value = isValid
        }
    }

    fun saveTask() {
        viewModelScope.launch(Dispatchers.IO) {
            if (taskId != null) {
                tasksRepository.updateTask(_taskEditScreenUiState.value.toTask(taskId.toInt()))
            } else {
                tasksRepository.insertTask(_taskEditScreenUiState.value.toTask())
            }
        }
    }
    //updateIsNotified
}