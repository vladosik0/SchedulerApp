package com.vladosik0.schedulerapp.presentation.view_models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladosik0.schedulerapp.data.local.repositories.TasksRepository
import com.vladosik0.schedulerapp.presentation.ui_state_converters.TaskUiStateElement
import com.vladosik0.schedulerapp.presentation.ui_state_converters.toTask
import com.vladosik0.schedulerapp.presentation.ui_state_converters.toTaskUiStateElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
            viewModelScope.launch(Dispatchers.IO) {
                tasksRepository.getTaskStream(taskId)
                    .map { TaskDetailsUiState.Success(it?.toTaskUiStateElement() ?: TaskUiStateElement()) }
                    .collect {
                        delay(1000)
                        _taskDetailsUiState.value = it
                    }
            }
        }
    }
    private val _taskDetailsUiState = MutableStateFlow<TaskDetailsUiState>(TaskDetailsUiState.Loading)
    val taskDetailsUiState: StateFlow<TaskDetailsUiState> = _taskDetailsUiState

    fun deleteTask(onDeleted: () -> Unit) {
        val currentState = _taskDetailsUiState.value
        if (currentState is TaskDetailsUiState.Success) {
            viewModelScope.launch {
                tasksRepository.deleteTask(currentState.task.toTask())
                delay(500)
                onDeleted()
            }
        }
    }

    fun updateTaskStatus() {
        val currentState = _taskDetailsUiState.value
        if(currentState is TaskDetailsUiState.Success) {
            val updatedTask = currentState.task.copy(isDone = !currentState.task.isDone)
            _taskDetailsUiState.value = TaskDetailsUiState.Success(task = updatedTask)
            viewModelScope.launch(Dispatchers.IO) {
                tasksRepository.updateTask(task = updatedTask.toTask())
            }
        }
    }
}

sealed class TaskDetailsUiState {
    object Loading : TaskDetailsUiState()
    data class Success(val task: TaskUiStateElement) : TaskDetailsUiState()
}