package com.vladosik0.schedulerapp.presentation.view_models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.vladosik0.schedulerapp.data.local.repositories.TasksRepository

class BuildScheduleScreenViewModel(
    tasksRepository: TasksRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val taskId: Int? = savedStateHandle["taskId"]

    val title: String? = savedStateHandle["title"]

    val description: String? = savedStateHandle["description"]

    val category: String? = savedStateHandle["category"]

    val priority: Int? = savedStateHandle["priority"]

    val difficulty: Int? = savedStateHandle["difficulty"]




}