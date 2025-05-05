package com.vladosik0.schedulerapp.presentation.view_models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladosik0.schedulerapp.data.local.repositories.TasksRepository
import com.vladosik0.schedulerapp.presentation.converters.TaskUiStateElement
import com.vladosik0.schedulerapp.presentation.converters.toTask
import com.vladosik0.schedulerapp.presentation.converters.toTaskUiStateElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TaskDetailsScreenViewModel(
    private val tasksRepository: TasksRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val taskId: Int? = savedStateHandle["taskId"]

    init {
        if(taskId != null) {
            viewModelScope.launch {
                tasksRepository.getTaskStream(taskId)
                    .map { TaskDetailsUiState.Success(it?.toTaskUiStateElement() ?: TaskUiStateElement()) }
                    .collect { _taskDetailsUiState.value = it }
            }
        }
    }
    private val _taskDetailsUiState = MutableStateFlow<TaskDetailsUiState>(TaskDetailsUiState.Loading)
    val taskDetailsUiState: StateFlow<TaskDetailsUiState> = _taskDetailsUiState

    private val _taskDetailsUiStateElement = MutableStateFlow(TaskUiStateElement())
    val taskDetailsUiStateElement: StateFlow<TaskUiStateElement> = _taskDetailsUiStateElement

    fun deleteTask() {
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.deleteTask(_taskDetailsUiStateElement.value.toTask())
        }
    }

    fun updateTaskStatus() {
        val isDone = _taskDetailsUiStateElement.value.isDone
        _taskDetailsUiStateElement.value = _taskDetailsUiStateElement.value.copy(isDone = !isDone)
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.updateTask(_taskDetailsUiStateElement.value.toTask())
        }
    }
}

sealed class TaskDetailsUiState {
    object Loading : TaskDetailsUiState()
    data class Success(val task: TaskUiStateElement) : TaskDetailsUiState()
}