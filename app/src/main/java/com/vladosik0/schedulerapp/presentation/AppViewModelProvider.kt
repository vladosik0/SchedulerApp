package com.vladosik0.schedulerapp.presentation

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vladosik0.schedulerapp.SchedulerApplication
import com.vladosik0.schedulerapp.presentation.view_models.DateScreenViewModel
import com.vladosik0.schedulerapp.presentation.view_models.SchedulerAppNavigationViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            SchedulerAppNavigationViewModel(
                tasksRepository = schedulerApplication().container.tasksRepository
            )
        }
        initializer {
            DateScreenViewModel(
                tasksRepository = schedulerApplication().container.tasksRepository
            )
        }
    }
}

fun CreationExtras.schedulerApplication(): SchedulerApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SchedulerApplication)