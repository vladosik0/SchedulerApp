package com.vladosik0.schedulerapp.data

import android.content.Context
import com.vladosik0.schedulerapp.data.local.SchedulerDatabase
import com.vladosik0.schedulerapp.data.local.repositories.OfflineTasksRepository
import com.vladosik0.schedulerapp.data.local.repositories.TasksRepository

interface AppContainer {
    val tasksRepository: TasksRepository
}

class DefaultAppContainer(context: Context): AppContainer {
    override val tasksRepository: TasksRepository by lazy {
        OfflineTasksRepository(SchedulerDatabase.getDatabase(context).taskDao())
    }
}