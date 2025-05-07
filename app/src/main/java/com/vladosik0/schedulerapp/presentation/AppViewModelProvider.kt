package com.vladosik0.schedulerapp.presentation

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vladosik0.schedulerapp.SchedulerApplication
import com.vladosik0.schedulerapp.presentation.view_models.SharedScheduleScreensViewModel
import com.vladosik0.schedulerapp.presentation.view_models.DateScreenViewModel
import com.vladosik0.schedulerapp.presentation.view_models.TaskDetailsScreenViewModel
import com.vladosik0.schedulerapp.presentation.view_models.TaskEditScreenViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            DateScreenViewModel(
                tasksRepository = schedulerApplication().container.tasksRepository
            )
        }
        initializer {
            TaskDetailsScreenViewModel(
                tasksRepository = schedulerApplication().container.tasksRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }
        initializer {
            TaskEditScreenViewModel(
                tasksRepository = schedulerApplication().container.tasksRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }
        initializer {
            SharedScheduleScreensViewModel(
                tasksRepository = schedulerApplication().container.tasksRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }
    }
}

fun CreationExtras.schedulerApplication(): SchedulerApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SchedulerApplication)