package com.vladosik0.schedulerapp.presentation.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vladosik0.schedulerapp.data.local.repositories.TasksRepository
import com.vladosik0.schedulerapp.presentation.converters.TaskUiStateElement
import com.vladosik0.schedulerapp.presentation.converters.toTask
import com.vladosik0.schedulerapp.presentation.converters.toTaskUiStateElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SchedulerAppNavigationViewModel(
    private val tasksRepository: TasksRepository
): ViewModel() {

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
            _editedTaskUiStateElement.value = task!!.toTaskUiStateElement()
        }
    }

    fun updateTaskStatus() {
        val isDone = _editedTaskUiStateElement.value.isDone
        _editedTaskUiStateElement.value = _editedTaskUiStateElement.value.copy(isDone = !isDone)
    }
}