package com.vladosik0.schedulerapp.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladosik0.schedulerapp.data.local.Task
import com.vladosik0.schedulerapp.data.local.repositories.TasksRepository
import com.vladosik0.schedulerapp.presentation.TaskUiStateElement
import com.vladosik0.schedulerapp.presentation.toTask
import com.vladosik0.schedulerapp.presentation.toTaskUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SchedulerAppNavigationViewModel(
    private val tasksRepository: TasksRepository
): ViewModel() {

    private val _tasks = MutableStateFlow(listOf<Task>())
    val tasks = _tasks
        .onStart { tasksRepository.getAllTasksStream() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            listOf()
        )

    private val _editedTaskUiStateElement = MutableStateFlow(TaskUiStateElement())
    val editedTaskUiStateElement: StateFlow<TaskUiStateElement> = _editedTaskUiStateElement

    fun updateTask() {
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.updateTask(_editedTaskUiStateElement.value.toTask())
        }
    }

    fun deleteTask() {
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.deleteTask(_editedTaskUiStateElement.value.toTask())
        }
    }

    fun createTask() {
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.insertTask(_editedTaskUiStateElement.value.toTask())
        }
    }

    fun updateUiStateById(taskId: Int) {
        viewModelScope.launch {
            val task = tasksRepository.getTaskStream(taskId).first()
            _editedTaskUiStateElement.value = task!!.toTaskUiState()
        }
    }

    fun updateTaskStatus() {
        val isDone = _editedTaskUiStateElement.value.isDone
        _editedTaskUiStateElement.value = _editedTaskUiStateElement.value.copy(isDone = !isDone)
    }
}

//data class DateScreenUiState(val tasks: List<TaskUiStateElement> = listOf())